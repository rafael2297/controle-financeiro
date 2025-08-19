document.addEventListener("DOMContentLoaded", async () => {
    const totalIncomesEl = document.getElementById("total-incomes");
    const totalExpensesEl = document.getElementById("total-expenses");
    const balanceEl = document.getElementById("balance");
    const historyContent = document.getElementById("report-history");

    // Filtros (apenas afetam o HISTÓRICO)
    const startDateEl = document.getElementById("start-date");
    const endDateEl = document.getElementById("end-date");
    const filterBtn = document.getElementById("filter-report");
    const clearBtn = document.getElementById("clear-filter");
    const filterCategoryEl = document.getElementById("filter-category");
    const filterTypeEl = document.getElementById("filter-type"); // pode não existir no HTML

    // Botão de exportar Excel
    const exportBtn = document.getElementById("export-excel");

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

    async function loadReport(startDate = null, endDate = null, categoryId = null, filterType = "") {
        try {
            const { incomes, expenses, balance, categories } = await fetchData();

            // Preenche o select de categorias uma única vez
            if (filterCategoryEl && filterCategoryEl.options.length <= 1) {
                categories.forEach(cat => {
                    const id = cat.id ?? cat.idCategoria;
                    const nome = cat.nome ?? cat.nomeCategoria;
                    if (id != null) filterCategoryEl.add(new Option(nome, id));
                });
            }

            // ====== HISTÓRICO (aplica filtros) ======
            let filteredIncomes = filterByDate(incomes, startDate, endDate, "dataReceita");
            let filteredExpenses = filterByDate(expenses, startDate, endDate, "dataDespesa");

            filteredIncomes = filterByCategory(filteredIncomes, categoryId, "idCategoria");
            filteredExpenses = filterByCategory(filteredExpenses, categoryId, "idCategoria");

            // filtro por tipo (receita/despesa)
            if (filterType === "receita") filteredExpenses = [];
            if (filterType === "despesa") filteredIncomes = [];

            // ====== RESUMO + PIZZA (NÃO sofrem filtros) ======
            const totalIncomes = incomes.reduce((sum, i) => sum + Number(i.valor), 0);
            const totalExpenses = expenses.reduce((sum, e) => sum + Number(e.valor), 0);

            totalIncomesEl.textContent = formatCurrency(totalIncomes);
            totalExpensesEl.textContent = "-" + formatCurrency(totalExpenses);

            balanceEl.textContent = formatCurrency(balance);
            balanceEl.style.color = balance < 0 ? "red" : "green";

            // === Gráfico de Pizza (como era antes) ===
            const ctx1 = document.getElementById("report-chart").getContext("2d");
            if (reportChart) reportChart.destroy();
            reportChart = new Chart(ctx1, {
                type: "pie",
                data: {
                    labels: ["Receitas", "Despesas"],
                    datasets: [{
                        data: [totalIncomes, totalExpenses],
                        backgroundColor: ["#16a34a", "#dc2626"]
                    }]
                },
                options: {
                    responsive: false,            // 🔙 como estava antes
                    maintainAspectRatio: false    // 🔙 como estava antes
                }
            });

            // === Histórico: gráfico de linha (nítido) ===
            const allData = [
                ...filteredIncomes.map(i => ({ ...i, tipo: "receita" })),
                ...filteredExpenses.map(e => ({ ...e, tipo: "despesa" }))
            ].sort((a, b) =>
                new Date(a.dataReceita ?? a.dataDespesa) - new Date(b.dataReceita ?? b.dataDespesa)
            );

            const labels = allData.map(d =>
                new Date(d.dataReceita ?? d.dataDespesa).toLocaleDateString("pt-BR")
            );
            const receitaVals = allData.map(d => d.tipo === "receita" ? Number(d.valor) : 0);
            const despesaVals = allData.map(d => d.tipo === "despesa" ? Number(d.valor) : 0);

            const ctx2 = document.getElementById("history-chart").getContext("2d");
            if (historyChart) historyChart.destroy();
            historyChart = new Chart(ctx2, {
                type: "line",
                data: {
                    labels,
                    datasets: [
                        { label: "Receitas", data: receitaVals, borderColor: "#16a34a", fill: false, tension: 0.25 },
                        { label: "Despesas", data: despesaVals, borderColor: "#dc2626", fill: false, tension: 0.25 }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    devicePixelRatio: window.devicePixelRatio || 1
                }
            });

            // === Lista do histórico (cards) ===
            historyContent.innerHTML = "";
            allData.forEach(d => {
                const card = document.createElement("div");
                card.className = "history-card";
                const date = new Date(d.dataReceita ?? d.dataDespesa).toLocaleDateString("pt-BR");
                const sinal = d.tipo === "receita" ? "+" : "-";

                card.innerHTML = `
                    <div>${d.descricao}</div>
                    <div class="valor" style="color:${d.tipo === "receita" ? "green" : "red"}">
                        ${sinal}&nbsp;${formatCurrency(d.valor)}
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

    // Eventos dos filtros (atuam só no HISTÓRICO)
    filterBtn?.addEventListener("click", async () => {
        await loadReport(
            startDateEl?.value || "",
            endDateEl?.value || "",
            filterCategoryEl?.value || "",
            (filterTypeEl?.value || "")
        );
    });

    clearBtn?.addEventListener("click", async () => {
        if (startDateEl) startDateEl.value = "";
        if (endDateEl) endDateEl.value = "";
        if (filterCategoryEl) filterCategoryEl.value = "";
        if (filterTypeEl) filterTypeEl.value = "";
        await loadReport();
    });

    // ✅ Exportar para Excel
    exportBtn?.addEventListener("click", async () => {
        try {
            const response = await fetch("http://localhost:8080/api/relatorios/excel");
            if (!response.ok) throw new Error("Erro ao exportar Excel");

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = "relatorio.xlsx";
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        } catch (err) {
            console.error("Erro ao baixar relatório:", err);
            alert("Erro ao exportar relatório para Excel!");
        }
    });

    // Inicialização
    await loadReport();
});
