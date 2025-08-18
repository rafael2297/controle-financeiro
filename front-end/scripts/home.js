document.addEventListener("DOMContentLoaded", () => {
    const saldoModal = document.getElementById("saldo-modal");
    const saldoForm = document.getElementById("saldo-form");
    const saldoAtualEl = document.getElementById("saldo-atual");

    function formatarMoeda(valor) {
        return new Intl.NumberFormat("pt-BR", {
            style: "currency",
            currency: "BRL"
        }).format(valor);
    }

    /**
     * Busca saldo atualizado do backend
     */
    async function carregarSaldo() {
        try {
            const response = await fetch("http://localhost:8080/api/saldos/atual");
            if (response.ok) {
                const valor = await response.json();
                saldoAtualEl.textContent = formatarMoeda(valor);
            } else {
                console.error("❌ Erro ao buscar saldo atual");
            }
        } catch (error) {
            console.error("❌ Erro de rede ao buscar saldo:", error);
        }
    }

    async function verificarSaldoExistente() {
        try {
            const response = await fetch("http://localhost:8080/api/saldos/existe");
            if (response.ok) {
                const existe = await response.json();
                if (!existe) {
                    saldoModal.classList.remove("hidden");
                } else {
                    carregarSaldo();
                }
            }
        } catch (error) {
            console.error("❌ Erro ao verificar saldo:", error);
        }
    }

    /**
     * Envia saldo inicial
     */
    saldoForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const valor = parseFloat(document.getElementById("saldo-inicial").value);

        try {
            const response = await fetch("http://localhost:8080/api/saldos", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ valorInicial: valor })
            });

            if (response.ok) {
                saldoModal.classList.add("hidden");
                carregarSaldo();
            } else {
                console.error("Erro ao salvar saldo inicial");
            }
        } catch (error) {
            console.error("Erro de rede ao salvar saldo:", error);
        }
    });

    /**
     * 🔹 Listener global para atualizar saldo em tempo real
     * Sempre que um evento customizado "saldo:atualizar" for disparado
     */
    document.addEventListener("saldo:atualizar", carregarSaldo);

    // Inicializa
    verificarSaldoExistente();
});
