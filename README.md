
# 💰 Controle Financeiro

Aplicativo **desktop** para controle de despesas, receitas, categorias e saldo, desenvolvido com **Electron (frontend)** e **Spring Boot (backend)**.  
O projeto empacota o **backend (JAR)** e o **JRE** junto com o app, permitindo que rode como um `.exe` sem precisar instalar Java.

---

## 🚀 Tecnologias

- **Frontend**: [Electron](https://www.electronjs.org/) + HTML + CSS + JavaScript  
- **Backend**: [Spring Boot](https://spring.io/projects/spring-boot) (empacotado em JAR)  
- **Banco de Dados**: configurável (MySQL ou H2 embutido)  
- **Empacotamento**: [electron-builder](https://www.electron.build/)

---

## 📂 Estrutura do Projeto

```

controle-financeiro/
├── backend/               # JAR do Spring Boot
├── jre/                   # JRE portátil usado no build
├── pages/                 # Páginas HTML do frontend
├── css/                   # Estilos da aplicação
├── scripts/               # Scripts JS do frontend
├── main.js                # Processo principal do Electron
├── package.json           # Configuração do projeto Electron
└── dist/                  # Pasta gerada no build (instalador .exe)

````

---

## 🔧 Como rodar em desenvolvimento

Clone o repositório e instale as dependências:

```sh
git clone https://github.com/rafael2297/controle-financeiro.git
cd controle-financeiro/front-end
npm install
````

Execute o projeto no modo dev:

```sh
npm start
```

O backend Spring Boot será iniciado automaticamente e o app abrirá em uma janela do Electron.

---

## 📦 Como gerar o instalador (.exe)

O projeto usa o **electron-builder**. Para criar o instalador:

```sh
npm run build
```

O instalador será gerado na pasta:

```
dist/
```

Você pode usar o arquivo `.exe` ou `.zip` gerado para instalar em qualquer computador **sem precisar instalar Java** (já que o JRE está embutido).

---

## 🗄️ Banco de Dados

O backend pode ser configurado para usar:

* **H2 Database (embutido)** → cria o banco automaticamente dentro da pasta `data/`
* **MySQL** → apontando para um banco local ou remoto via `application.properties`

Exemplo `application.properties` com H2:

```properties
spring.datasource.url=jdbc:h2:file:./data/banco
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
```

---

## ✨ Funcionalidades

* ✅ Cadastro de **despesas**
* ✅ Cadastro de **receitas**
* ✅ Cadastro de **categorias**
* ✅ Exibição do **saldo atual**
* ✅ Relatórios e histórico
* ✅ App funciona como **.exe standalone**

## 👨‍💻 Autor

Desenvolvido por **Rafael**
📌 [GitHub](https://github.com/rafael2297)




