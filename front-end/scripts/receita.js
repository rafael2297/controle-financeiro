document.addEventListener("DOMContentLoaded", () => {
    const incomeList = document.getElementById("income-list");
    const addIncomeForm = document.getElementById("add-income-form");
    const refreshIncomesBtn = document.getElementById("refresh-incomes");

    // Selects
    const createCategory = document.getElementById("income-category");
    const editCategory = document.getElementById("edit-income-category");

    // Modal edição
    const editModal = document.getElementById("edit-income-modal");
    const closeEditBtn = document.getElementById("close-edit-income");
    const cancelEditBtn = document.getElementById("cancel-edit-income");
    const editIncomeForm = document.getElementById("edit-income-form");

    // Inputs edição
    const editId = document.getElementById("edit-income-id");
    const editDescription = document.getElementById("edit-income-description");
    const editAmount = document.getElementById("edit-income-amount");
    const editDate = document.getElementById("edit-income-date");
    const editCategoryInput = document.getElementById("edit-income-category");

    // --- MAPA DE CATEGORIAS ---
    const categoriesMap = new Map();

    function formatCurrency(value) {
        return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(value ?? 0);
    }

    async function updateBalance() {
        try {
            const response = await fetch("http://localhost:8080/api/saldos/atual");
            if (response.ok) {
                const valor = await response.json();
                const balanceEl = document.getElementById("current-balance");

                balanceEl.innerText = formatCurrency(valor);

                if (valor < 0) {
                    balanceEl.style.color = "red";
                } else {
                    balanceEl.style.color = "green";
                }
            } else {
                console.error("❌ Erro ao buscar saldo atual");
            }
        } catch (error) {
            console.error("❌ Erro de rede ao buscar saldo:", error);
        }
    }

    async function loadCategories() {
        try {
            const res = await fetch("http://localhost:8080/api/categorias");
            const categories = await res.json();

            if (createCategory) {
                createCategory.innerHTML = `<option value="" disabled selected>Selecione uma categoria</option>`;
            }
            if (editCategory) {
                editCategory.innerHTML = `<option value="" disabled selected>Selecione uma categoria</option>`;
            }

            categoriesMap.clear();

            categories.forEach(cat => {
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
            console.error("Erro ao carregar categorias (receitas):", err);
        }
    }

    function toInputDate(value) {
        if (!value) return "";
        if (value.includes("T")) return value.split("T")[0];
        return value;
    }

    // abrir modal de edição
    function openEditIncome(income) {
        editId.value = income.id;
        editDescription.value = income.descricao ?? "";
        editAmount.value = income.valor ?? "";
        editDate.value = toInputDate(income.dataReceita);
        editCategoryInput.value = income.idCategoria ?? "";

        editModal.classList.add("flex");
        editModal.classList.remove("hidden");
    }

    function closeEditModal() {
        editModal.classList.remove("flex");
        editModal.classList.add("hidden");
    }

    closeEditBtn?.addEventListener("click", closeEditModal);
    cancelEditBtn?.addEventListener("click", closeEditModal);

    // carregar receitas
    async function loadIncomes() {
        try {
            const res = await fetch("http://localhost:8080/api/receitas");
            const incomes = await res.json();

            incomeList.innerHTML = "";
            incomes.forEach(inc => {
                const li = document.createElement("li");
                li.className = "income-card";

                const dataBr = inc.dataReceita
                    ? new Date(inc.dataReceita).toLocaleDateString("pt-BR")
                    : "";

                const nomeCat = categoriesMap.get(String(inc.idCategoria)) || "Não informada";

                li.innerHTML = `
                    <div>${inc.descricao}</div>
                    <div class="income-value">${formatCurrency(inc.valor)}</div>
                    <div>${dataBr}</div>
                    <div>${nomeCat}</div>
                    <div class="flex gap-2 justify-center">
                        <button class="edit-button-list">✏️</button>
                        <button class="delete-button-list">🗑️</button>
                    </div>
                `;

                li.querySelector(".edit-button-list").addEventListener("click", () => openEditIncome(inc));
                li.querySelector(".delete-button-list").addEventListener("click", async () => {
                    await fetch(`http://localhost:8080/api/receitas/${inc.id}`, { method: "DELETE" });
                    await loadIncomes();
                    await updateBalance();
                });

                incomeList.appendChild(li);
            });

            await updateBalance();
            closeEditModal();
        } catch (err) {
            console.error("Erro ao carregar receitas:", err);
        }
    }

    // enviar edição
    editIncomeForm?.addEventListener("submit", async (e) => {
        e.preventDefault();

        const payload = {
            descricao: editDescription.value,
            valor: parseFloat(editAmount.value),
            dataReceita: editDate.value,
            idCategoria: parseInt(editCategoryInput.value)
        };

        try {
            const resp = await fetch(`http://localhost:8080/api/receitas/${editId.value}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            if (!resp.ok) {
                console.error("Falha ao atualizar:", await resp.text());
                return;
            }

            closeEditModal();
            await loadIncomes();
        } catch (err) {
            console.error("Erro ao atualizar receita:", err);
        }
    });

    // adicionar nova receita
    addIncomeForm?.addEventListener("submit", async (e) => {
        e.preventDefault();

        const description = document.getElementById("income-description").value;
        const amount = parseFloat(document.getElementById("income-amount").value);
        const date = document.getElementById("income-date").value;
        const categoryId = parseInt(createCategory.value);

        if (!categoryId || Number.isNaN(categoryId)) {
            alert("Por favor, selecione uma categoria.");
            return;
        }

        const payload = {
            descricao: description,
            valor: amount,
            dataReceita: date,
            idCategoria: categoryId
        };

        try {
            const resp = await fetch("http://localhost:8080/api/receitas", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            if (!resp.ok) {
                console.error("Falha ao adicionar:", await resp.text());
                return;
            }

            addIncomeForm.reset();
            if (createCategory) createCategory.selectedIndex = 0;

            await loadIncomes();
        } catch (err) {
            console.error("Erro de rede ao adicionar receita:", err);
        }
    });

    // botão de atualizar
    refreshIncomesBtn?.addEventListener("click", async () => {
        await loadCategories();
        await loadIncomes();
    });

    // inicialização
    (async () => {
        await loadCategories();
        await loadIncomes();
    })();
});
