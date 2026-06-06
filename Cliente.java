public class Cliente {
    public String cpf;
    public String nome;
    public String telefone;
    public String email;

    public Cliente() {}

    public Cliente(String cpf, String nome, String telefone, String email) {
        this.cpf = cpf;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    public void imprime() {
        System.out.println("CPF: " + cpf + " | Nome: " + nome +
                           " | Telefone: " + telefone + " | Email: " + email);
    }
}