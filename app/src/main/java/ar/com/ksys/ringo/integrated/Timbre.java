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
    private boolean modoFueraDeCasa;

    public Timbre(boolean estaActivado,boolean modoFueraDeCasa) {
        this.estaActivado = estaActivado;
        this.modoFueraDeCasa = modoFueraDeCasa;
    }

    public Timbre(){}

    public boolean isEstaActivado() {
        return estaActivado;
    }

    public void setEstaActivado(boolean estaActivado) {
        this.estaActivado = estaActivado;
    }

    public boolean isModoFueraDeCasa(){return modoFueraDeCasa;}

    public void setModoFueraDeCasa(boolean modoFueraDeCasa){this.modoFueraDeCasa = modoFueraDeCasa;}

    public void activarTimbre(Context context){
        this.setEstaActivado(true);
        Toast.makeText(context, "timbre activado", Toast.LENGTH_SHORT).show();
    }

    public void desactivarTimbre(Context context){
        this.setEstaActivado(false);
        Toast.makeText(context, "timbre desactivado", Toast.LENGTH_SHORT).show();
    }

    public void activarModoFueraDeCasa(Context context){
        this.setModoFueraDeCasa(true);
        Toast.makeText(context, "modo fuera de casa activado", Toast.LENGTH_SHORT).show();
    }

    public void desactivarModoFueradeCasa(Context context){
        this.setModoFueraDeCasa(false);
        Toast.makeText(context, "modo fuera de casa desactivado", Toast.LENGTH_SHORT).show();
    }


}
