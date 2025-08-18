const API_HISTORICO = "http://localhost:8080/api/saldos/historico";
let historyChart;

// Carregar histórico mensal
async function loadHistorico() {
    try {
        const res = await fetch(API_HISTORICO);
        if (!res.ok) throw new Error("Erro ao carregar histórico");
        const historico = await res.json();

        // Ordena os meses
        historico.sort((a, b) => a.mes.localeCompare(b.mes));

        const labels = historico.map(h => h.mes);
        const receitas = historico.map(h => h.totalReceitas);
        const despesas = historico.map(h => h.totalDespesas);
        const saldo = historico.map(h => h.saldoFinal);

        renderHistoryChart(labels, receitas, despesas, saldo);
    } catch (err) {
        console.error("Erro ao carregar histórico:", err);
    }
}

function renderHistoryChart(labels, receitas, despesas, saldo) {
    const ctx = document.getElementById("history-chart").getContext("2d");

    if (historyChart) {
        historyChart.destroy();
    }

    historyChart = new Chart(ctx, {
        type: "line",
        data: {
            labels,
            datasets: [
                {
                    label: "Receitas",
                    data: receitas,
                    borderColor: "#16a34a",
                    backgroundColor: "rgba(22,163,74,0.2)",
                    fill: true
                },
                {
                    label: "Despesas",
                    data: despesas,
                    borderColor: "#dc2626",
                    backgroundColor: "rgba(220,38,38,0.2)",
                    fill: true
                },
                {
                    label: "Saldo",
                    data: saldo,
                    borderColor: "#2563eb",
                    backgroundColor: "rgba(37,99,235,0.2)",
                    fill: true
                }
            ]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: "bottom"
                },
                title: {
                    display: true,
                    text: "Evolução Mensal"
                }
            }
        }
    });
}

// Inicializa
document.addEventListener("DOMContentLoaded", () => {
    loadResumo();
    loadHistorico();
});
