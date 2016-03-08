package ar.com.ksys.ringo.integrated;

import android.content.Context;
import android.widget.Toast;



import android.content.Context;
import android.widget.Toast;

/**
 * Created by Usuario on 24/04/2015.
 */
public class Timbre {
    private boolean estaActivado;
    private boolean tieneSonido;


    public Timbre(boolean estaActivado, boolean tieneSonido) {
        this.estaActivado = estaActivado;
        this.tieneSonido = tieneSonido;
    }

    public Timbre(){}

    public boolean isEstaActivado() {
        return estaActivado;
    }

    public void setEstaActivado(boolean estaActivado) {
        this.estaActivado = estaActivado;
    }

    public boolean isTieneSonido() {
        return tieneSonido;
    }

    public void setTieneSonido(boolean tieneSonido) {
        this.tieneSonido = tieneSonido;
    }

    public void activarTimbre(Context context){
        this.setEstaActivado(true);
        Toast.makeText(context, "timbre activado: " + this.isEstaActivado(), Toast.LENGTH_LONG).show();
    }

    public void desactivarTimbre(Context context){
        this.setEstaActivado(false);
        Toast.makeText(context, "timbre activado: " + this.isEstaActivado(), Toast.LENGTH_LONG).show();
    }

    public void activarSonido(Context context){
        this.setTieneSonido(true);
        Toast.makeText(context, "sonido activado: " + this.isTieneSonido(), Toast.LENGTH_LONG).show();
    }

    public void desactivarSonido(Context context){
        this.setTieneSonido(false);
        Toast.makeText(context, "sonido activado: " + this.isTieneSonido(), Toast.LENGTH_LONG).show();
    }


}
