# 📱 Crypto Now - Documentação do Aplicativo

## 🧭 Visão Geral
**Crypto Now** é um aplicativo Android desenvolvido para fornecer informações em tempo real sobre criptomoedas, incluindo preços, variações percentuais e capitalização de mercado.  
O app exibe uma lista de criptomoedas em alta (**Top Gainers**) e uma seção de **Tendências (Trending)**, permitindo que os usuários acompanhem o mercado de forma prática e visual.

---

## 🎯 Objetivo
Oferecer uma interface simples e intuitiva para entusiastas de criptomoedas, com atualizações frequentes de dados de mercado e detalhes sobre moedas específicas.

---

## 👥 Público-Alvo
- Usuários interessados em criptomoedas e investimentos.
- Investidores que buscam acompanhar preços e tendências em tempo real.
- Entusiastas de tecnologia que desejam explorar dados de mercado de forma acessível.

---

## 🔍 Funcionalidades

### 📈 Exibição de Criptomoedas
- **Top Gainers**: Lista horizontal com as 3 criptomoedas com maior variação positiva nas últimas 24 horas.
- **Trending**: Lista vertical com várias criptomoedas ordenadas por capitalização de mercado, exibindo:
  - Preço atual  
  - Variação percentual  
  - Capitalização de mercado  

### 🔄 Atualização em Tempo Real
- Preços atualizados **a cada segundo** via API da Binance.
- Variações percentuais atualizadas **a cada 15 segundos**.

### 📊 Detalhes da Criptomoeda
- Ao clicar em uma moeda da lista **Trending**, o usuário é levado à **tela de detalhes** *(em desenvolvimento)*.

### 🌙 Modo Noturno
- Suporte a **tema escuro** com base nas preferências do sistema.

### 🪙 Ícone Personalizado
- Ícone adaptativo com o logotipo **Crypto Now**, compatível com Android 8.0+.

---

## 🧱 Arquitetura do Aplicativo

com.example.cryptonow ├── activities │ ├── MainActivity.java │ └── DetailActivity.java (em desenvolvimento) ├── adapters │ ├── TopGainerAdapter.java │ └── CryptoAdapter.java ├── models │ └── Crypto.java ├── res/layout │ ├── activity_main.xml │ ├── item_top_gainer.xml │ └── item_crypto.xml ├── res/mipmap │ └── ic_launcher (ícones em múltiplas densidades) ├── res/drawable │ └── logo_crypto_now.png

markdown
Copiar
Editar

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java  
- **Plataforma**: Android (API mínima: 21 - Android 5.0 Lollipop)  
- **Bibliotecas**:
  - `OkHttp`: Requisições HTTP
  - `Gson`: Parsing de JSON
  - `RecyclerView`: Exibição de listas

### 🌐 APIs
- **Binance API**:
  - `/api/v3/ticker/price` – Preços em tempo real
  - `/api/v3/ticker/24hr` – Variações percentuais em 24h

- **CoinGecko API**:
  - `/api/v3/coins/markets` – URLs de ícones e capitalização de mercado

---

## 🔁 Fluxo de Dados

### Inicialização
- Carrega preferências (modo noturno)  
- Requisição inicial à **CoinGecko** para ícones e capitalização  

### Atualizações
- **A cada 1 segundo**: Preços via **Binance**
- **A cada 15 segundos**: Variações percentuais via **Binance**
- Listas de "Top Gainers" e "Trending" são atualizadas com os dados recebidos

---

## 🚀 Instruções de Uso

### 🔧 Instalação
1. Clone o repositório ou abra o projeto no Android Studio  
2. Certifique-se de que o SDK está configurado (API 21+)  
3. Conecte um dispositivo ou configure um emulador  
4. Compile e execute via `Run > Run 'app'`

### 📱 Uso do App
- Tela principal mostra o **logo**, **Top Gainers**, e **Trending**
- Role para ver mais moedas
- Clique em uma criptomoeda para abrir a tela de detalhes (em breve)

### 🌓 Modo Noturno
- Ativado automaticamente conforme o sistema
- Pode ser configurado manualmente nas preferências do código

---

## 🧩 Manutenção e Extensões

### 🔮 Possíveis Melhorias
- **DetailActivity**: Exibir gráfico de preços, volume, etc  
- **Pesquisa**: Filtro por nome/símbolo  
- **Favoritos**: Marcar moedas favoritas  
- **Notificações**: Alertas de variação de preço  
- **Suporte Offline**: Cache local de dados

### 🐞 Problemas Conhecidos
- **Limites da API**: Pode causar falhas em altas requisições  
- **Performance**: Atualizações frequentes consomem bateria/dados  
- **Detalhes**: Tela ainda não implementada  

### 🧰 Manutenção
- Atualizar bibliotecas (OkHttp, Gson) periodicamente  
- Monitorar mudanças nas APIs  
- Adicionar testes unitários e testar em múltiplos dispositivos
