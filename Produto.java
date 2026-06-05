public class Produto {
    public int codigo;
    public String nome;
    public double preco;
    public int estoque;

    public Produto() {}

    public Produto(int codigo, String nome, double preco, int estoque) {
        this.codigo = codigo;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    public void imprime() {
        System.out.println("Codigo: " + codigo + " | Nome: " + nome +
                           " | Preco: R$ " + String.format("%.2f", preco) +
                           " | Estoque: " + estoque);
    }
}