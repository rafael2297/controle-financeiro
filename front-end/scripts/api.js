// scripts/api.js

// URL base do back-end Spring Boot
const API_BASE_URL = 'http://localhost:8080/api';

/**
 * Verifica se o back-end está ativo.
 */
export async function checkBackendStatus() {
    console.log('Verificando status do back-end...');
    let retries = 0;
    const maxRetries = 20;
    const delay = 500; // 500ms

    while (retries < maxRetries) {
        try {
            const response = await fetch(`${API_BASE_URL}/despesas`);
            if (response.ok) {
                console.log('Back-end está ativo!');
                return true;
            }
        } catch (error) {
            console.warn(`Tentativa ${retries + 1} de conexão falhou. Re-tentando...`);
        }
        await new Promise(res => setTimeout(res, delay));
        retries++;
    }
    console.error('Falha ao conectar-se ao back-end após várias tentativas.');
    return false;
}

/**
 * Busca todas as despesas do back-end.
 */
export async function getExpenses() {
    try {
        const response = await fetch(`${API_BASE_URL}/despesas`);
        if (!response.ok) throw new Error('Falha ao buscar despesas.');
        return await response.json();
    } catch (error) {
        console.error('Erro ao obter despesas:', error);
        return [];
    }
}

/**
 * Adiciona uma nova despesa ao back-end.
 */
export async function addExpense(expenseData) {
    try {
        const payload = {
            descricao: expenseData.description,
            valor: expenseData.amount,
            dataDespesa: expenseData.date,
            idCategoria: expenseData.category,
            pagamento: expenseData.payment || "Dinheiro"
        };

        const response = await fetch(`${API_BASE_URL}/despesas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });

        if (!response.ok) throw new Error('Falha ao adicionar despesa.');
        return await response.json();
    } catch (error) {
        console.error('Erro ao adicionar despesa:', error);
        return null;
    }
}

/**
 * Busca todas as receitas do back-end.
 */
export async function getIncomes() {
    try {
        const response = await fetch(`${API_BASE_URL}/receitas`);
        if (!response.ok) throw new Error('Falha ao buscar receitas.');
        return await response.json();
    } catch (error) {
        console.error('Erro ao obter receitas:', error);
        return [];
    }
}

/**
 * Adiciona uma nova receita ao back-end.
 */
export async function addIncome(incomeData) {
    try {
        const payload = {
            descricaoReceita: incomeData.description,
            valorRecebido: incomeData.amount,
            dataReceita: incomeData.date,
            idCategoria: incomeData.category,
            pagamento: incomeData.payment || "Dinheiro"
        };

        const response = await fetch(`${API_BASE_URL}/receitas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
        });

        if (!response.ok) throw new Error('Falha ao adicionar receita.');
        return await response.json();
    } catch (error) {
        console.error('Erro ao adicionar receita:', error);
        return null;
    }
}

/**
 * Busca todas as categorias no back-end.
 */
export async function getCategories() {
    try {
        const response = await fetch(`${API_BASE_URL}/categorias`);
        if (!response.ok) throw new Error('Falha ao buscar categorias.');
        return await response.json();
    } catch (error) {
        console.error('Erro ao obter categorias:', error);
        return [];
    }
}
