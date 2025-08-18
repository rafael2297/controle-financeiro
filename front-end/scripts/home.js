document.addEventListener("DOMContentLoaded", () => {
    const saldoModal = document.getElementById("saldo-modal");
    const saldoForm = document.getElementById("saldo-form");
    const saldoAtualEl = document.getElementById("saldo-atual");

    /**
     * Formata o valor em BRL
     */
    function formatarMoeda(valor) {
        return new Intl.NumberFormat("pt-BR", {
            style: "currency",
            currency: "BRL"
        }).format(valor);
    }

    /**
     * Busca saldo atual no backend e atualiza o front
     */
    async function carregarSaldo() {
        try {
            const response = await fetch("http://localhost:8080/api/saldos/atual");
            if (response.ok) {
                const valor = await response.json();
                console.log("✅ Saldo atual recebido do backend:", valor); // <--- DEBUG
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
                console.log("ℹ️ Existe saldo cadastrado?", existe); // <--- DEBUG
                if (!existe) {
                    saldoModal.classList.remove("hidden");
                } else {
                    carregarSaldo(); // já existe saldo, carrega direto
                }
            }
        } catch (error) {
            console.error("❌ Erro ao verificar saldo:", error);
        }
    }


    /**
     * Evento submit do formulário do saldo inicial
     */
    saldoForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const valor = parseFloat(document.getElementById("saldo-inicial").value);

        try {
            const response = await fetch("http://localhost:8080/api/saldos", { // ✅ URL absoluta
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ valorInicial: valor })
            });

            if (response.ok) {
                saldoModal.classList.add("hidden"); // esconde modal
                carregarSaldo(); // atualiza saldo exibido
            } else {
                console.error("Erro ao salvar saldo inicial");
            }
        } catch (error) {
            console.error("Erro de rede ao salvar saldo:", error);
        }
    });

    // Inicializa ao carregar página
    verificarSaldoExistente();
});
