// scripts/receita.js
document.addEventListener("DOMContentLoaded", async () => {
    const incomeList = document.getElementById("income-list");
    const categorySelect = document.getElementById("income-category");
    const addBtn = document.getElementById("add-income");
    const refreshBtn = document.getElementById("refresh-incomes");

    async function loadCategories() {
        const categories = await window.electronAPI.getCategories();
        categorySelect.innerHTML = "";
        categories.forEach(cat => {
            const opt = document.createElement("option");
            opt.value = cat.id;
            opt.textContent = cat.nome;
            categorySelect.appendChild(opt);
        });
    }

    async function loadIncomes() {
        const incomes = await window.electronAPI.getIncomes();
        incomeList.innerHTML = "";
        incomes.forEach(inc => {
            const li = document.createElement("li");
            li.textContent = `${inc.descricaoReceita} - R$${inc.valorRecebido} - ${inc.dataReceita}`;
            incomeList.appendChild(li);
        });
    }

    addBtn.addEventListener("click", async () => {
        const description = document.getElementById("income-description").value;
        const amount = parseFloat(document.getElementById("income-amount").value);
        const date = document.getElementById("income-date").value;
        const category = categorySelect.value;

        if (!description || !amount || !date || !category) {
            alert("Preencha todos os campos!");
            return;
        }

        await window.electronAPI.addIncome({
            description,
            amount,
            date,
            category,
        });

        await loadIncomes();
    });

    refreshBtn.addEventListener("click", loadIncomes);

    await loadCategories();
    await loadIncomes();
});
