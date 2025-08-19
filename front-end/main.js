const { app, BrowserWindow, ipcMain } = require("electron");
const path = require("path");
const { spawn, exec } = require("child_process");

let backendProcess;
let mainWindow;

/**
 * Função para resolver paths no modo dev e no build (.exe).
 */
const getResourcePath = (relativePath) => {
    if (app.isPackaged) {
        return path.join(process.resourcesPath, relativePath);
    } else {
        return path.join(__dirname, relativePath);
    }
};

/**
 * Inicia o backend Spring Boot (arquivo JAR) usando o JRE empacotado.
 */
function startBackend() {
    const jarPath = getResourcePath("backend/controle-despesas-0.0.1-SNAPSHOT.jar");
    const javaPath = getResourcePath("jre/bin/java.exe");

    backendProcess = spawn(javaPath, ["-jar", jarPath], {
        cwd: path.dirname(jarPath),
        detached: false,
    });

    backendProcess.stdout.on("data", (data) => {
        console.log(`BACKEND: ${data}`);
    });

    backendProcess.stderr.on("data", (data) => {
        console.error(`BACKEND ERROR: ${data}`);
    });

    backendProcess.on("exit", (code) => {
        console.log(`Backend finalizado com código ${code}`);
    });
}

/**
 * Encerra o backend quando o app fecha.
 */
function stopBackend() {
    if (backendProcess && backendProcess.pid) {
        console.log(`Encerrando backend (PID: ${backendProcess.pid})`);

        if (process.platform === "win32") {
            exec(`taskkill /PID ${backendProcess.pid} /F /T`, (err) => {
                if (err) {
                    console.error("Erro ao encerrar backend:", err);
                } else {
                    console.log("Backend encerrado com sucesso");
                }
            });
        } else {
            backendProcess.kill("SIGTERM");
            console.log("Backend encerrado (Linux/Mac)");
        }
    }
}

/**
 * Cria a janela principal do app.
 */
function createWindow() {
    mainWindow = new BrowserWindow({
        width: 1200,
        height: 800,
        webPreferences: {
            preload: path.join(__dirname, "preload.js"),
            nodeIntegration: false,
            contextIsolation: true,
        },
    });

    // Carrega página inicial
    mainWindow.loadFile(path.join(__dirname, "pages", "index.html"));

    mainWindow.on("closed", () => {
        mainWindow = null;
    });
}

/**
 * Eventos do ciclo de vida do Electron.
 */
app.on("ready", () => {
    startBackend();
    setTimeout(createWindow, 3000); // espera o backend iniciar
});

app.on("window-all-closed", () => {
    stopBackend();
    if (process.platform !== "darwin") {
        app.quit();
    }
});

app.on("before-quit", () => {
    stopBackend();
});

// ========== CANAIS DE COMUNICAÇÃO (IPC) ==========
ipcMain.handle("ping", () => "pong");

// navegação entre páginas
ipcMain.on("navigate", (event, page) => {
    mainWindow.loadFile(path.join(__dirname, "pages", page));
});

// refresh forçado
ipcMain.on("refresh-data", () => {
    mainWindow.reload();
});

// Função fetch compatível com ESM
const fetch = (...args) => import("node-fetch").then(({ default: fetch }) => fetch(...args));
const API_BASE = "http://localhost:8080/api";

// ================= DESPESAS =================
ipcMain.handle("get-expenses", async () => {
    try {
        const response = await fetch(`${API_BASE}/despesas`);
        if (!response.ok) throw new Error("Erro ao buscar despesas");
        return await response.json();
    } catch (err) {
        console.error("Erro em get-expenses:", err);
        throw err;
    }
});

ipcMain.handle("add-expense", async (event, expense) => {
    try {
        const response = await fetch(`${API_BASE}/despesas`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(expense),
        });
        return await response.json();
    } catch (err) {
        console.error("Erro em add-expense:", err);
        throw err;
    }
});

ipcMain.handle("update-expense", async (event, { id, expense }) => {
    try {
        const response = await fetch(`${API_BASE}/despesas/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(expense),
        });
        return await response.json();
    } catch (err) {
        console.error("Erro em update-expense:", err);
        throw err;
    }
});

ipcMain.handle("delete-expense", async (event, id) => {
    try {
        const response = await fetch(`${API_BASE}/despesas/${id}`, {
            method: "DELETE",
        });
        return response.ok;
    } catch (err) {
        console.error("Erro em delete-expense:", err);
        throw err;
    }
});

// ================= CATEGORIAS =================
ipcMain.handle("get-categories", async () => {
    try {
        const response = await fetch(`${API_BASE}/categorias`);
        return await response.json();
    } catch (err) {
        console.error("Erro em get-categories:", err);
        throw err;
    }
});

ipcMain.handle("add-category", async (event, category) => {
    try {
        const response = await fetch(`${API_BASE}/categorias`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(category),
        });
        return await response.json();
    } catch (err) {
        console.error("Erro em add-category:", err);
        throw err;
    }
});

// ================= RECEITAS =================
ipcMain.handle("get-receitas", async () => {
    const res = await fetch(`${API_BASE}/receitas`);
    return res.json();
});

ipcMain.handle("add-receita", async (event, receita) => {
    const res = await fetch(`${API_BASE}/receitas`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(receita),
    });
    return res.json();
});

ipcMain.handle("update-receita", async (event, { id, receita }) => {
    const res = await fetch(`${API_BASE}/receitas/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(receita),
    });
    return res.json();
});

ipcMain.handle("delete-receita", async (event, id) => {
    await fetch(`${API_BASE}/receitas/${id}`, { method: "DELETE" });
    return { success: true };
});

// ================= SALDO =================
ipcMain.handle("get-saldo", async () => {
    try {
        const response = await fetch(`${API_BASE}/saldo`);
        return await response.json();
    } catch (err) {
        console.error("Erro em get-saldo:", err);
        throw err;
    }
});
