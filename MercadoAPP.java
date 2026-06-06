import java.util.Scanner;

public class MercadoAPP {
    static Produto[] produtos;
    static Cliente[] clientes;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        produtos = CsvUtil.ler("produtos.csv", Produto.class);
        clientes = CsvUtil.ler("clientes.csv", Cliente.class);

        if (produtos == null) produtos = new Produto[0];
        if (clientes == null) clientes = new Cliente[0];

        int op = -1;
        while (op != 0) {
            Screen.clear();
            System.out.println("===== MINI MERCADO =====");
            System.out.println("1 - Produtos");
            System.out.println("2 - Clientes");
            System.out.println("3 - Realizar Compra");
            System.out.println("4 - Controle de Estoque");
            System.out.println("0 - Sair");
            System.out.print("Digite opcao: ");
            String line = sc.nextLine();
            try {
                op = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                op = -1;
            }

            switch (op) {
                case 1: menuProdutos(); break;
                case 2: menuClientes(); break;
                case 3: realizarCompra(); break;
                case 4: listarEstoque(); break;
                case 0: System.out.println("Saindo..."); break;
                default:
                    System.out.println("Opcao invalida! Pressione ENTER para continuar...");
                    sc.nextLine();
            }
        }
        sc.close();
    }

    // ---------- MENU PRODUTOS ----------
    public static void menuProdutos() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            Screen.clear();
            System.out.println("===== PRODUTOS =====");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Listar");
            System.out.println("3 - Buscar");
            System.out.println("4 - Alterar");
            System.out.println("5 - Remover");
            System.out.println("0 - Voltar");
            System.out.print("Opcao: ");
            String op = sc.nextLine();
            if (op.equals("1")) cadastrarProduto();
            else if (op.equals("2")) { listarProdutos(); pause(); }
            else if (op.equals("3")) { buscarProduto(); pause(); }
            else if (op.equals("4")) { alterarProduto(); pause(); }
            else if (op.equals("5")) { removerProduto(); pause(); }
            else if (op.equals("0")) break;
            else { System.out.println("Opcao invalida."); pause(); }
        }
    }

    // ---------- MENU CLIENTES ----------
    public static void menuClientes() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            Screen.clear();
            System.out.println("===== CLIENTES =====");
            System.out.println("1 - Cadastrar");
            System.out.println("2 - Listar");
            System.out.println("3 - Buscar");
            System.out.println("4 - Alterar");
            System.out.println("5 - Remover");
            System.out.println("0 - Voltar");
            System.out.print("Opcao: ");
            String op = sc.nextLine();
            if (op.equals("1")) cadastrarCliente();
            else if (op.equals("2")) { listarClientes(); pause(); }
            else if (op.equals("3")) { buscarCliente(); pause(); }
            else if (op.equals("4")) { alterarCliente(); pause(); }
            else if (op.equals("5")) { removerCliente(); pause(); }
            else if (op.equals("0")) break;
            else { System.out.println("Opcao invalida."); pause(); }
        }
    }

    // ---------- REALIZAR COMPRA ----------
    public static void realizarCompra() {
        Scanner sc = new Scanner(System.in);
        System.out.println("===== REALIZAR COMPRA =====");
        System.out.print("Sou cadastrado? (S/N): ");
        String resp = sc.nextLine().trim();
        Cliente cliente = null;

        if (resp.equalsIgnoreCase("S")) {
            System.out.print("Digite CPF: ");
            String cpf = sc.nextLine().trim();
            cliente = buscarClientePorCpf(cpf);
            if (cliente == null) {
                System.out.println("Cliente nao encontrado. Pressione ENTER para continuar...");
                sc.nextLine();
                return;
            }
        } else {
            System.out.println("Cadastro rapido:");
            System.out.print("CPF: "); String cpf = sc.nextLine().trim();
            System.out.print("Nome: "); String nome = sc.nextLine().trim();
            System.out.print("Telefone: "); String tel = sc.nextLine().trim();
            System.out.print("Email: "); String email = sc.nextLine().trim();
            cliente = new Cliente(cpf, nome, tel, email);
            adicionarCliente(cliente);
            System.out.println("Cliente cadastrado com sucesso.");
        }

        double total = 0;
        StringBuilder relatorio = new StringBuilder();

        while (true) {
            System.out.println();
            listarProdutos();
            System.out.print("Digite codigo do produto (-1 para finalizar): ");
            String sCod = sc.nextLine();
            int cod;
            try { cod = Integer.parseInt(sCod); } catch (NumberFormatException e) { System.out.println("Codigo invalido."); continue; }
            if (cod == -1) break;

            Produto p = buscarProdutoPorCodigo(cod);
            if (p == null) { System.out.println("Produto nao encontrado."); continue; }

            System.out.println("Produto: " + p.nome + " | Preco: R$ " + String.format("%.2f", p.preco) + " | Estoque: " + p.estoque);
            System.out.print("Quantidade: ");
            String sQt = sc.nextLine();
            int qt;
            try { qt = Integer.parseInt(sQt); } catch (NumberFormatException e) { System.out.println("Quantidade invalida."); continue; }
            if (qt <= 0) { System.out.println("Quantidade deve ser maior que zero."); continue; }
            if (qt > p.estoque) { System.out.println("Estoque insuficiente! Disponivel: " + p.estoque); continue; }

            double subtotal = qt * p.preco;
            total += subtotal;
            relatorio.append(p.codigo).append(" | ").append(p.nome).append(" | ").append(qt)
                     .append(" | R$ ").append(String.format("%.2f", p.preco))
                     .append(" | R$ ").append(String.format("%.2f", subtotal)).append("\n");

            // reduzir estoque
            p.estoque -= qt;
            // salvar estoque atualizado
            CsvUtil.salvar(produtos, "produtos.csv");
            System.out.println("Item adicionado. Subtotal: R$ " + String.format("%.2f", subtotal));
        }

        System.out.println("\n===== NOTA FISCAL =====");
        if (cliente != null) System.out.println("Cliente: " + cliente.nome + " | CPF: " + cliente.cpf);
        System.out.println("Itens:");
        System.out.println("Codigo | Nome | Qtde | Valor Unit. | Subtotal");
        System.out.println(relatorio.toString());
        System.out.println("TOTAL: R$ " + String.format("%.2f", total));
        System.out.println("Compra finalizada. Pressione ENTER para continuar...");
        sc.nextLine();
    }

    // ---------- CONTROLE DE ESTOQUE ----------
    public static void listarEstoque() {
        Screen.clear();
        System.out.println("===== ESTOQUE =====");
        if (produtos.length == 0) System.out.println("Nenhum produto cadastrado.");
        for (Produto p : produtos) p.imprime();
        pause();
    }

    // ---------- CRUD PRODUTOS ----------
    public static void cadastrarProduto() {
        Scanner sc = new Scanner(System.in);
        System.out.println("===== CADASTRAR PRODUTO =====");
        try {
            System.out.print("Codigo (inteiro): "); int cod = Integer.parseInt(sc.nextLine().trim());
            if (buscarProdutoPorCodigo(cod) != null) {
                System.out.println("Codigo ja existe. Operacao cancelada.");
                pause(); return;
            }
            System.out.print("Nome: "); String nome = sc.nextLine().trim();
            System.out.print("Preco: "); double preco = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Estoque: "); int est = Integer.parseInt(sc.nextLine().trim());

            Produto novo = new Produto(cod, nome, preco, est);
            Produto[] nova = new Produto[produtos.length + 1];
            for (int i = 0; i < produtos.length; i++) nova[i] = produtos[i];
            nova[produtos.length] = novo;
            produtos = nova;
            CsvUtil.salvar(produtos, "produtos.csv");
            System.out.println("Produto cadastrado com sucesso.");
        } catch (NumberFormatException e) {
            System.out.println("Entrada numerica invalida. Operacao cancelada.");
        }
    }

    public static void listarProdutos() {
        if (produtos.length == 0) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        for (Produto p : produtos) p.imprime();
    }

    public static void buscarProduto() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Digite codigo: ");
        try {
            int cod = Integer.parseInt(sc.nextLine().trim());
            Produto p = buscarProdutoPorCodigo(cod);
            if (p != null) p.imprime(); else System.out.println("Produto nao encontrado!");
        } catch (NumberFormatException e) {
            System.out.println("Codigo invalido.");
        }
    }

    public static void alterarProduto() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Digite codigo: ");
        try {
            int cod = Integer.parseInt(sc.nextLine().trim());
            Produto p = buscarProdutoPorCodigo(cod);
            if (p == null) { System.out.println("Produto nao encontrado."); return; }
            System.out.print("Novo nome (enter para manter): ");
            String nome = sc.nextLine();
            if (!nome.isEmpty()) p.nome = nome;
            System.out.print("Novo preco (enter para manter): ");
            String precoStr = sc.nextLine();
            if (!precoStr.isEmpty()) p.preco = Double.parseDouble(precoStr);
            System.out.print("Novo estoque (enter para manter): ");
            String estStr = sc.nextLine();
            if (!estStr.isEmpty()) p.estoque = Integer.parseInt(estStr);
            CsvUtil.salvar(produtos, "produtos.csv");
            System.out.println("Produto alterado com sucesso.");
        } catch (NumberFormatException e) {
            System.out.println("Entrada invalida.");
        }
    }

    public static void removerProduto() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Digite codigo: ");
        try {
            int cod = Integer.parseInt(sc.nextLine().trim());
            Produto p = buscarProdutoPorCodigo(cod);
            if (p == null) { System.out.println("Produto nao encontrado."); return; }
            Produto[] nova = new Produto[produtos.length - 1];
            int idx = 0;
            for (Produto prod : produtos) {
                if (prod.codigo != cod) nova[idx++] = prod;
            }
            produtos = nova;
            CsvUtil.salvar(produtos, "produtos.csv");
            System.out.println("Produto removido.");
        } catch (NumberFormatException e) {
            System.out.println("Codigo invalido.");
        } catch (NegativeArraySizeException e) {
            System.out.println("Nao ha produtos para remover.");
        }
    }

    // ---------- CRUD CLIENTES ----------
    public static void cadastrarCliente() {
        Scanner sc = new Scanner(System.in);
        System.out.println("===== CADASTRAR CLIENTE =====");
        System.out.print("CPF: "); String cpf = sc.nextLine().trim();
        if (buscarClientePorCpf(cpf) != null) { System.out.println("CPF ja cadastrado."); return; }
        System.out.print("Nome: "); String nome = sc.nextLine().trim();
        System.out.print("Telefone: "); String tel = sc.nextLine().trim();
        System.out.print("Email: "); String email = sc.nextLine().trim();
        Cliente novo = new Cliente(cpf, nome, tel, email);
        Cliente[] nova = new Cliente[clientes.length + 1];
        for (int i = 0; i < clientes.length; i++) nova[i] = clientes[i];
        nova[clientes.length] = novo;
        clientes = nova;
        CsvUtil.salvar(clientes, "clientes.csv");
        System.out.println("Cliente cadastrado com sucesso.");
        
    }

    public static void listarClientes() {
        if (clientes.length == 0) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        for (Cliente c : clientes) c.imprime();
    }

    public static void buscarCliente() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Digite CPF: ");
        String cpf = sc.nextLine().trim();
        Cliente c = buscarClientePorCpf(cpf);
        if (c != null) c.imprime(); else System.out.println("Cliente nao encontrado.");
    }

    public static void alterarCliente() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Digite CPF do cliente a alterar: ");
        String cpf = sc.nextLine().trim();
        Cliente c = buscarClientePorCpf(cpf);
        if (c == null) { System.out.println("Cliente nao encontrado."); return; }
        System.out.print("Novo nome (enter para manter): ");
        String nome = sc.nextLine();
        if (!nome.isEmpty()) c.nome = nome;
        System.out.print("Novo telefone (enter para manter): ");
        String tel = sc.nextLine();
        if (!tel.isEmpty()) c.telefone = tel;
        System.out.print("Novo email (enter para manter): ");
        String email = sc.nextLine();
        if (!email.isEmpty()) c.email = email;
        CsvUtil.salvar(clientes, "clientes.csv");
        System.out.println("Cliente alterado com sucesso.");
    }

    public static void removerCliente() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Digite CPF: ");
        String cpf = sc.nextLine().trim();
        Cliente c = buscarClientePorCpf(cpf);
        if (c == null) { System.out.println("Cliente nao encontrado."); return; }
        Cliente[] nova = new Cliente[clientes.length - 1];
        int idx = 0;
        for (Cliente cli : clientes) {
            if (!cli.cpf.equals(cpf)) nova[idx++] = cli;
        }
        clientes = nova;
        CsvUtil.salvar(clientes, "clientes.csv");
        System.out.println("Cliente removido.");
    }

    // ---------- utilitários ----------
    private static Produto buscarProdutoPorCodigo(int codigo) {
        for (Produto p : produtos) if (p.codigo == codigo) return p;
        return null;
    }

    private static Cliente buscarClientePorCpf(String cpf) {
        for (Cliente c : clientes) if (c.cpf != null && c.cpf.equals(cpf)) return c;
        return null;
    }

    private static void adicionarCliente(Cliente c) {
        Cliente[] nova = new Cliente[clientes.length + 1];
        for (int i = 0; i < clientes.length; i++) nova[i] = clientes[i];
        nova[clientes.length] = c;
        clientes = nova;
        CsvUtil.salvar(clientes, "clientes.csv");
    }

    private static void pause() {
        System.out.println("Pressione ENTER para continuar...");
        try { System.in.read(); } catch (Exception e) { /* ignora */ }
    }
}