const API_BASE = "http://localhost:8080/api/categorias";

// --- LISTAR CATEGORIAS ---
async function loadCategories() {
    try {
        const res = await fetch(API_BASE);
        if (!res.ok) throw new Error("Erro ao carregar categorias");
        const categories = await res.json();

        const list = document.getElementById("category-list");
        list.innerHTML = "";

        categories.forEach(cat => {
            const li = document.createElement("li");
            li.className = "category-card";

            li.innerHTML = `
                <span class="font-medium">${cat.nome}</span>
                <div class="flex gap-2">
                    <button class="edit-button-list" data-id="${cat.id}">✏️</button>
                    <button class="delete-button-list" data-id="${cat.id}">🗑️</button>
                </div>
            `;

            // editar
            li.querySelector(".edit-button-list").addEventListener("click", () => openEditModal(cat));

            // excluir
            li.querySelector(".delete-button-list").addEventListener("click", async () => {
                if (confirm(`Deseja excluir a categoria "${cat.nome}"?`)) {
                    await fetch(`${API_BASE}/${cat.id}`, { method: "DELETE" });
                    loadCategories();
                }
            });

            list.appendChild(li);
        });
    } catch (err) {
        console.error("Erro:", err);
        alert("Erro ao carregar categorias.");
    }
}


// --- ADICIONAR CATEGORIA ---
document.getElementById("add-category-form").addEventListener("submit", async (e) => {
    e.preventDefault();

    const nome = document.getElementById("category-name").value.trim();
    if (!nome) {
        alert("O nome da categoria não pode ser vazio.");
        return;
    }

    try {
        const res = await fetch(API_BASE, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ nome })
        });

        if (!res.ok) {
            alert("Erro ao adicionar categoria.");
            return;
        }

        document.getElementById("category-name").value = "";
        loadCategories();
    } catch (err) {
        console.error("Erro:", err);
        alert("Erro ao adicionar categoria.");
    }
});

// --- INICIALIZAÇÃO ---
document.getElementById("refresh-categories").addEventListener("click", loadCategories);
document.addEventListener("DOMContentLoaded", loadCategories);

const editModal = document.getElementById("edit-category-modal");
const cancelEditBtn = document.getElementById("cancel-edit-category");
const closeEditBtn = document.getElementById("close-edit-modal");
const editForm = document.getElementById("edit-category-form");

function openEditModal(category) {
    document.getElementById("edit-category-id").value = category.id;
    document.getElementById("edit-category-name").value = category.nome;

    // abre com nossa classe própria
    editModal.classList.add("open");
    editModal.setAttribute("aria-hidden", "false");
}

function closeEditModal() {
    editModal.classList.remove("open");
    editModal.setAttribute("aria-hidden", "true");
}

cancelEditBtn.addEventListener("click", closeEditModal);
closeEditBtn.addEventListener("click", closeEditModal);

// fechar ao clicar fora do card
editModal.addEventListener("click", (e) => {
    if (e.target === editModal) closeEditModal();
});


