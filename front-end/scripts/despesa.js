document.addEventListener("DOMContentLoaded", () => {
    const expenseList = document.getElementById("expense-list");
    const addExpenseForm = document.getElementById("add-expense-form");
    const refreshExpensesBtn = document.getElementById("refresh-expenses");

    const editModal = document.getElementById("edit-expense-modal");
    const closeEditBtn = document.getElementById("close-edit-expense");
    const cancelEditBtn = document.getElementById("cancel-edit-expense");
    const editExpenseForm = document.getElementById("edit-expense-form");

    // Inputs de edição
    const editId = document.getElementById("edit-expense-id");
    const editDescription = document.getElementById("edit-expense-description");
    const editAmount = document.getElementById("edit-expense-amount");
    const editDate = document.getElementById("edit-expense-date");
    const editCategory = document.getElementById("edit-expense-category");
    const editPayment = document.getElementById("edit-expense-payment");

    // Selects do formulário de criação
    const createCategory = document.getElementById("expense-category");
    const createPayment = document.getElementById("expense-payment");

    // Mapa de categorias id->nome (como string para chave estável)
    const categoriesMap = new Map();

    function formatCurrency(value) {
        return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(value ?? 0);
    }

    function getCategoryName(id) {
        if (id === null || id === undefined) return "Não informada";
        return categoriesMap.get(String(id)) || "Não informada";
    }

    function renderExpense(expense) {
        const li = document.createElement("li");
        li.className = "expense-card flex justify-between items-center";

        const dataBr = expense.dataDespesa
            ? new Date(expense.dataDespesa).toLocaleDateString("pt-BR")
            : "";

        const nomeCat = expense.categoriaNome || getCategoryName(expense.idCategoria);

        li.innerHTML = `
      <div>
        <p class="font-bold">${expense.descricao}</p>
        <p class="text-gray-600">${formatCurrency(expense.valor)}</p>
        <p class="text-sm text-gray-500">${dataBr}</p>
        <p class="text-sm text-gray-500">Categoria: ${nomeCat}</p>
        <p class="text-sm text-gray-500">Pagamento: ${expense.pagamento || "-"}</p>
      </div>
      <div class="flex gap-2">
        <button class="edit-button-list">✏️</button>
        <button class="delete-button-list">🗑️</button>
      </div>
    `;

        li.querySelector(".edit-button-list").addEventListener("click", () => openEditExpense(expense));
        li.querySelector(".delete-button-list").addEventListener("click", async () => {
            if (confirm("Deseja excluir esta despesa?")) {
                await fetch(`http://localhost:8080/api/despesas/${expense.id}`, { method: "DELETE" });

                // ✅ garante que nenhum modal fique aberto
                editModal.classList.remove("flex");
                editModal.classList.add("hidden");

                loadExpenses();
            }
        });


        return li;
    }

    function closeEditModal() {
        editModal.classList.remove("flex");
        editModal.classList.add("hidden");
    }

    async function loadExpenses() {
        try {
            const res = await fetch("http://localhost:8080/api/despesas");
            const expenses = await res.json();

            expenseList.innerHTML = "";
            expenses.forEach(exp => {
                expenseList.appendChild(renderExpense(exp));
            });

            // ✅ força fechamento do modal para não travar a UI
            closeEditModal();
        } catch (err) {
            console.error("Erro ao carregar despesas:", err);
        }
    }

    async function loadCategories() {
        try {
            const res = await fetch("http://localhost:8080/api/categorias");
            const categories = await res.json();

            // Limpa e repovoa selects
            if (createCategory) {
                createCategory.innerHTML = `<option value="" disabled selected>Selecione uma categoria</option>`;
            }
            if (editCategory) {
                editCategory.innerHTML = `<option value="" disabled selected>Selecione uma categoria</option>`;
            }

            categoriesMap.clear();

            categories.forEach(cat => {
                // Fallback para diferentes formatos do backend
                const id = cat.id ?? cat.idCategoria ?? cat.id_categoria;
                const nome = cat.nome ?? cat.nomeCategoria ?? cat.nome_categoria ?? "";

                if (id != null) {
                    categoriesMap.set(String(id), nome);

                    if (createCategory) {
                        createCategory.add(new Option(nome, id));
                    }
                    if (editCategory) {
                        editCategory.add(new Option(nome, id));
                    }
                }
            });
        } catch (err) {
            console.error("Erro ao carregar categorias:", err);
        }
    }

    function toInputDate(value) {
        // aceita "2025-08-17" ou "2025-08-17T00:00:00"
        if (!value) return "";
        if (value.includes("T")) return value.split("T")[0];
        return value;
    }

    function openEditExpense(expense) {
        editId.value = expense.id;
        editDescription.value = expense.descricao ?? "";
        editAmount.value = expense.valor ?? "";
        editDate.value = toInputDate(expense.dataDespesa);
        editCategory.value = expense.idCategoria ?? "";
        editPayment.value = expense.pagamento || "DINHEIRO";

        editModal.classList.add("flex");
        editModal.classList.remove("hidden");
    }

    function closeEditModal() {
        editModal.classList.remove("flex");
        editModal.classList.add("hidden");
    }

    closeEditBtn?.addEventListener("click", closeEditModal);
    cancelEditBtn?.addEventListener("click", closeEditModal);

    editExpenseForm?.addEventListener("submit", async (e) => {
        e.preventDefault();

        const payload = {
            descricao: editDescription.value,
            valor: parseFloat(editAmount.value),
            dataDespesa: editDate.value,              // yyyy-MM-dd
            idCategoria: parseInt(editCategory.value),
            pagamento: editPayment.value
        };

        try {
            const resp = await fetch(`http://localhost:8080/api/despesas/${editId.value}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            if (!resp.ok) {
                console.error("Falha ao atualizar:", await resp.text());
                return;
            }

            closeEditModal();
            await loadExpenses();
        } catch (err) {
            console.error("Erro ao atualizar despesa:", err);
        }
    });

    addExpenseForm?.addEventListener("submit", async (e) => {
        e.preventDefault();

        const description = document.getElementById("expense-description").value;
        const amount = parseFloat(document.getElementById("expense-amount").value);
        const date = document.getElementById("expense-date").value;
        const categoryId = parseInt(createCategory.value);
        const payment = createPayment?.value || "DINHEIRO";

        if (!categoryId || Number.isNaN(categoryId)) {
            alert("Por favor, selecione uma categoria.");
            return;
        }

        const payload = {
            descricao: description,
            valor: amount,
            dataDespesa: date,       // yyyy-MM-dd
            idCategoria: categoryId,
            pagamento: payment
        };

        try {
            const resp = await fetch("http://localhost:8080/api/despesas", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            if (!resp.ok) {
                console.error("Falha ao adicionar:", await resp.text());
                return;
            }

            addExpenseForm.reset();
            if (createCategory) createCategory.selectedIndex = 0;
            if (createPayment) createPayment.selectedIndex = 0;

            await loadExpenses();
        } catch (err) {
            console.error("Erro de rede ao adicionar despesa:", err);
        }
    });

    async function deleteExpense(id) {
        if (!confirm("Deseja excluir esta despesa?")) return;

        try {
            const resp = await fetch(`http://localhost:8080/api/despesas/${id}`, { method: "DELETE" });
            if (!resp.ok) {
                console.error("Falha ao excluir:", await resp.text());
                return;
            }
            await loadExpenses(); // mantém tudo funcionando depois do delete
        } catch (err) {
            console.error("Erro de rede ao excluir despesa:", err);
        }
    }

    refreshExpensesBtn?.addEventListener("click", async () => {
        await loadCategories(); // garante nomes de categoria sempre disponíveis
        await loadExpenses();
    });

    // Inicialização
    (async () => {
        await loadCategories();
        await loadExpenses();
    })();
});
