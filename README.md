# Margem Atenta

Projeto da Global Solution de Programação Orientada a Objetos. O Margem Atenta
simula o monitoramento de risco de alagamento na Vila das Pontes, uma região
fictícia. Pelo console, é possível coletar medições, consultar o histórico,
estimar a próxima leitura e verificar os alertas registrados.

**Repositório:** [ArtFurlan/Margem-Atenta](https://github.com/ArtFurlan/Margem-Atenta)

## Público beneficiado

O sistema foi pensado para equipes municipais de monitoramento e Defesa Civil
que precisam acompanhar regiões próximas a córregos. Os alertas ajudam a
identificar quais medições merecem atenção e podem apoiar a decisão de vistoriar
um ponto monitorado.

## Como executar

1. Clone este repositório ou extraia o ZIP da entrega.
2. Abra a pasta do projeto no IntelliJ IDEA e selecione o JDK 17.
3. Importe o projeto Maven pelo `pom.xml` e aguarde o download das dependências.
4. Execute o método `main` da classe `margematenta.SistemaMonitoramento`.

Na primeira execução, o sistema cria o banco H2 na pasta `dados/`. As medições
e os alertas ficam salvos ali e podem ser consultados depois de reiniciar o
programa.

## Menu

| Opção | O que faz |
| --- | --- |
| 1 | Verifica os sensores e registra as medições válidas. |
| 2 | Consulta o histórico de medições por sensor. |
| 3 | Executa uma previsão e registra um alerta quando um limiar é atingido. |
| 4 | Consulta o histórico de alertas. |
| 5 | Consulta e altera os limiares por sensor. |
| 6 | Mostra os dados da região, o status da estação e os limiares atuais. |
| 0 | Encerra o programa. |

Em uma instalação nova, use a opção **1** cinco vezes para formar o histórico
inicial. Depois, use a opção **3**, escolha um sensor e selecione o método de
previsão. Se a previsão atingir um limiar, o alerta aparecerá também na opção
**4**.

O código de acesso de demonstração da opção **5** é `1234`. Os limiares
alterados valem durante a execução atual. Ao reiniciar, o programa carrega
os valores padrão.

## Decisões de implementação

- **Sensores.** O sistema usa a mesma interface `Sensor` para chuva acumulada
  em milímetros, nível do córrego em metros e umidade do solo em porcentagem.
  Cada sensor informa seu tipo e fornece uma leitura simulada.
- **Validação.** `DadosInvalidosException` trata, por exemplo, medições fora
  da faixa e alterações de limiares inválidas. `FonteIndisponivelException`
  indica quando um sensor não pode fornecer uma leitura.
- **Persistência.** O H2 em arquivo permite consultar o histórico após reiniciar.
  Os DAOs usam JDBC e `PreparedStatement` para salvar e consultar os registros.
- **Previsões.** A média móvel usa as cinco leituras mais recentes. A comparação
  de períodos calcula a média das duas mais recentes e projeta a diferença
  em relação à média das três anteriores.
- **Factory.** A `FabricaPrevisao` cria a estratégia escolhida no menu. Assim,
  o serviço pode usar o mesmo método `prever` para as duas estratégias, sem
  precisar saber como cada uma é criada.
- **Alertas.** O sistema compara a estimativa com os limiares de atenção,
  alerta e crítico. Quando mais de um limiar é atingido, registra o nível
  mais alto e mostra o sensor, o valor estimado e o limiar atingido.

## Testes automatizados

No IntelliJ, abra **Maven → Lifecycle → test**. Se tiver o Maven instalado
no terminal, também pode rodar `mvn test` na raiz do projeto.

A suíte tem sete testes com JUnit. Ela verifica os cálculos das previsões,
a rejeição de medições negativas, o histórico insuficiente e o bloqueio de
alterações com código de acesso incorreto. Os testes com Mockito simulam os
DAOs para verificar quando o serviço deve salvar um alerta e quando deve
deixar o histórico intacto.

## Reflexão crítica

As leituras são geradas pelo programa quando o usuário verifica os sensores.
Elas servem para exercitar o fluxo de coleta, previsão e alerta. Como os
valores são simulados e as coletas dependem de quando o usuário abre o menu,
duas leituras seguidas podem representar intervalos de tempo diferentes.
Por isso, a próxima leitura estimada deve ser interpretada dentro da
demonstração, sem associá-la a um horário ou a uma chance real de alagamento.

A escolha de exigir cinco medições evita que o programa apresente uma previsão
quando há pouco histórico para aplicar os métodos. Esse mínimo permite
executar os cálculos, mas a qualidade da estimativa ainda depende da qualidade
dos dados. A média móvel pode suavizar uma subida brusca do córrego, enquanto
a comparação de períodos pode dar peso excessivo a uma oscilação nas duas
leituras mais recentes. Os limiares também foram definidos para a simulação.
Em um uso real, precisariam ser ajustados com dados locais e critérios de
quem acompanha a região.

O papel do alerta é chamar atenção para uma situação que merece verificação.
Uma equipe responsável pode usar o histórico para entender por que o alerta
apareceu e conferir outras informações antes de agir, como a condição do
córrego e os avisos oficiais. A decisão de vistoriar a área ou orientar os
moradores continua com a equipe responsável.

## Uso de inteligência artificial

Usei o ChatGPT para me ajudar a escolher a versão do H2, organizar o projeto 
e tirar dúvidas sobre as previsões. Também pedi ajuda para comparar formas de 
criar as funcionalidades e ver exemplos de código e testes que me serviram de 
base, além de encontrar erros simples, como uma letra a mais ou uma vírgula no 
lugar errado.

## Integrantes e divisão de tarefas

| Integrante | RM     | Contribuição |
| --- |--------| --- |
| Arthur | 564665 | Estruturação do projeto Java, implementação dos sensores, persistência com JDBC e H2, estratégias de previsão, alertas e funcionalidades do menu. |
| Vinicius | 563791 | Implementação e execução dos testes automatizados com JUnit e Mockito e elaboração do README com as instruções de execução e teste. |