package aplication.android.wimervm.appterapias;

public class citas {

    String fecha,fechacalendario,hora,id,id_paciente,procedimiento;

    public citas() {
    }

    public citas(String fecha, String fechacalendario, String hora, String id, String id_paciente, String procedimiento) {
        this.fecha = fecha;
        this.fechacalendario = fechacalendario;
        this.hora = hora;
        this.id = id;
        this.id_paciente = id_paciente;
        this.procedimiento = procedimiento;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFechacalendario() {
        return fechacalendario;
    }

    public void setFechacalendario(String fechacalendario) {
        this.fechacalendario = fechacalendario;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_paciente() {
        return id_paciente;
    }

    public void setId_paciente(String id_paciente) {
        this.id_paciente = id_paciente;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }
}
