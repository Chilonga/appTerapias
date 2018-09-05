package aplication.android.wimervm.appterapias;

public class Pacientes {

    String nombre, apellido, id, cedula, correo, seguro, nss, id_tutor;

    public Pacientes() {
    }

    public Pacientes(String nombre, String apellido, String id, String cedula, String correo, String seguro, String nss, String id_tutor) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.id = id;
        this.cedula = cedula;
        this.correo = correo;
        this.seguro = seguro;
        this.nss = nss;
        this.id_tutor = id_tutor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getSeguro() {
        return seguro;
    }

    public void setSeguro(String seguro) {
        this.seguro = seguro;
    }

    public String getNss() {
        return nss;
    }

    public void setNss(String nss) {
        this.nss = nss;
    }

    public String getId_tutor() {
        return id_tutor;
    }

    public void setId_tutor(String id_tutor) {
        this.id_tutor = id_tutor;
    }
}
