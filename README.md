
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

---


📅 **Última Atualização**: 29 de abril de 2025
