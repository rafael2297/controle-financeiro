const { contextBridge, ipcRenderer } = require("electron");

// Aqui você expõe APIs seguras para o front
contextBridge.exposeInMainWorld("electronAPI", {
    // --- Teste simples ---
    ping: () => ipcRenderer.invoke("ping"),

    // --- NAVEGAÇÃO ENTRE PÁGINAS ---
    navigate: (page) => ipcRenderer.send("navigate", page),

    // --- DESPESAS ---
    getExpenses: () => ipcRenderer.invoke("get-expenses"),
    addExpense: (expense) => ipcRenderer.invoke("add-expense", expense),
    updateExpense: (id, expense) => ipcRenderer.invoke("update-expense", { id, expense }),
    deleteExpense: (id) => ipcRenderer.invoke("delete-expense", id),

    // --- RECEITAS ---
    getReceitas: () => ipcRenderer.invoke("get-receitas"),
    addReceita: (receita) => ipcRenderer.invoke("add-receita", receita),

    // --- CATEGORIAS ---
    getCategories: () => ipcRenderer.invoke("get-categories"),
    addCategory: (category) => ipcRenderer.invoke("add-category", category),

    // --- SALDO ---
    getSaldo: () => ipcRenderer.invoke("get-saldo"),

    // --- REFRESH ---
    refreshData: () => ipcRenderer.send("refresh-data"),
});
