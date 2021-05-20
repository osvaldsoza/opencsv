public class Candidate {
    private String name;
    private String vaga;
    private String idade;
    private String estado;

    public Candidate(String name, String vaga, String idade, String estado) {
        this.name = name;
        this.vaga = vaga;
        this.idade = idade;
        this.estado = estado;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVaga() {
        return vaga;
    }

    public void setVaga(String vaga) {
        this.vaga = vaga;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "name='" + name + '\'' +
                ", vaga='" + vaga + '\'' +
                ", idade='" + idade + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
