document.addEventListener("DOMContentLoaded", async () => {
    const totalIncomesEl = document.getElementById("total-incomes");
    const totalExpensesEl = document.getElementById("total-expenses");
    const balanceEl = document.getElementById("balance");
    const historyContent = document.getElementById("report-history");

    // Filtros
    const startDateEl = document.getElementById("start-date");
    const endDateEl = document.getElementById("end-date");
    const filterBtn = document.getElementById("filter-report");
    const clearBtn = document.getElementById("clear-filter");
    const filterCategoryEl = document.getElementById("filter-category");

    let reportChart, historyChart;

    function formatCurrency(value) {
        return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(value ?? 0);
    }

    async function fetchData() {
        const [incomesRes, expensesRes, balanceRes, categoriesRes] = await Promise.all([
            fetch("http://localhost:8080/api/receitas"),
            fetch("http://localhost:8080/api/despesas"),
            fetch("http://localhost:8080/api/saldos/atual"),
            fetch("http://localhost:8080/api/categorias")
        ]);

        return {
            incomes: await incomesRes.json(),
            expenses: await expensesRes.json(),
            balance: await balanceRes.json(),
            categories: await categoriesRes.json()
        };
    }

    function filterByDate(data, startDate, endDate, dateField) {
        return data.filter(item => {
            const date = new Date(item[dateField]);
            if (startDate && date < new Date(startDate)) return false;
            if (endDate && date > new Date(endDate)) return false;
            return true;
        });
    }

    function filterByCategory(data, categoryId, field) {
        if (!categoryId) return data;
        return data.filter(item => String(item[field]) === String(categoryId));
    }

    async function loadReport(startDate = null, endDate = null, categoryId = null) {
        try {
            const { incomes, expenses, balance, categories } = await fetchData();

            // Preencher categorias no filtro
            if (filterCategoryEl.options.length <= 1) {
                categories.forEach(cat => {
                    const id = cat.id ?? cat.idCategoria;
                    const nome = cat.nome ?? cat.nomeCategoria;
                    filterCategoryEl.add(new Option(nome, id));
                });
            }

            // Aplicar filtros
            let filteredIncomes = filterByDate(incomes, startDate, endDate, "dataReceita");
            let filteredExpenses = filterByDate(expenses, startDate, endDate, "dataDespesa");

            filteredIncomes = filterByCategory(filteredIncomes, categoryId, "idCategoria");
            filteredExpenses = filterByCategory(filteredExpenses, categoryId, "idCategoria");

            // Totais
            const totalIncomes = filteredIncomes.reduce((sum, i) => sum + Number(i.valor), 0);
            const totalExpenses = filteredExpenses.reduce((sum, e) => sum + Number(e.valor), 0);

            totalIncomesEl.textContent = formatCurrency(totalIncomes);
            totalExpensesEl.textContent = formatCurrency(totalExpenses);
            balanceEl.textContent = formatCurrency(balance);

            // Gráfico de pizza
            const ctx1 = document.getElementById("report-chart").getContext("2d");
            if (reportChart) reportChart.destroy();
            reportChart = new Chart(ctx1, {
                type: "pie",
                data: {
                    labels: ["Receitas", "Despesas"],
                    datasets: [{ data: [totalIncomes, totalExpenses], backgroundColor: ["#16a34a", "#dc2626"] }]
                },
                options: { responsive: false, maintainAspectRatio: false }
            });

            // Histórico (gráfico + lista)
            const allData = [
                ...filteredIncomes.map(i => ({ ...i, tipo: "receita" })),
                ...filteredExpenses.map(e => ({ ...e, tipo: "despesa" }))
            ].sort((a, b) => new Date(a.dataReceita ?? a.dataDespesa) - new Date(b.dataReceita ?? b.dataDespesa));

            const labels = allData.map(d => new Date(d.dataReceita ?? d.dataDespesa).toLocaleDateString("pt-BR"));
            const receitaVals = allData.map(d => d.tipo === "receita" ? d.valor : 0);
            const despesaVals = allData.map(d => d.tipo === "despesa" ? d.valor : 0);

            const ctx2 = document.getElementById("history-chart").getContext("2d");
            if (historyChart) historyChart.destroy();
            historyChart = new Chart(ctx2, {
                type: "line",
                data: {
                    labels,
                    datasets: [
                        { label: "Receitas", data: receitaVals, borderColor: "#16a34a", fill: false },
                        { label: "Despesas", data: despesaVals, borderColor: "#dc2626", fill: false }
                    ]
                },
                options: { responsive: false, maintainAspectRatio: false }
            });

            // Render cards do histórico
            historyContent.innerHTML = "";
            allData.forEach(d => {
                const card = document.createElement("div");
                card.className = "history-card";
                const date = new Date(d.dataReceita ?? d.dataDespesa).toLocaleDateString("pt-BR");

                card.innerHTML = `
          <div>${d.descricao}</div>
          <div class="valor" style="color:${d.tipo === "receita" ? "green" : "red"}">
            ${d.tipo === "receita" ? "+" : "-"}${formatCurrency(d.valor)}
          </div>
          <div>${date}</div>
          <div>${d.tipo === "receita" ? "Receita" : "Despesa"}</div>
        `;

                historyContent.appendChild(card);
            });
        } catch (err) {
            console.error("Erro ao carregar relatório:", err);
        }
    }

    // Filtros
    filterBtn?.addEventListener("click", async () => {
        await loadReport(startDateEl.value, endDateEl.value, filterCategoryEl.value);
    });

    clearBtn?.addEventListener("click", async () => {
        startDateEl.value = "";
        endDateEl.value = "";
        filterCategoryEl.value = "";
        await loadReport();
    });

    // Inicialização
    await loadReport();
});
