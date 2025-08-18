import { checkBackendStatus } from "./api.js";

document.addEventListener("DOMContentLoaded", async () => {
    const loadingScreen = document.getElementById("loading-screen");
    const mainApp = document.getElementById("main-app");
    const progressBar = document.querySelector(".progress-bar");

    // Função para simular progresso
    const simulateLoading = () => {
        return new Promise((resolve) => {
            let progress = 0;
            const interval = setInterval(() => {
                progress += 5;
                progressBar.style.width = progress + "%";
                if (progress >= 100) {
                    clearInterval(interval);
                    resolve();
                }
            }, 100);
        });
    };

    await simulateLoading();
    await checkBackendStatus();

    // Esconde tela de loading e mostra app
    loadingScreen.style.display = "none";
    mainApp.style.display = "block";
});
