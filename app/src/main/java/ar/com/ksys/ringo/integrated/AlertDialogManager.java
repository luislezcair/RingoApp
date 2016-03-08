package ar.com.ksys.ringo.integrated;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class AlertDialogManager {
    /**
     * Function to display simple Alert Dialog
     * @param contexto - application context
     * @param titulo - alert dialog title
     * @param mensaje - alert message
     *
     * */
    public void showAlertDialog(Context contexto, String titulo, String mensaje,
                                Boolean estado) {
        AlertDialog alertDialog = new AlertDialog.Builder(contexto).create();

        // Setting Dialog Title
        alertDialog.setTitle(titulo);

        // Setting Dialog Message
        alertDialog.setMessage(mensaje);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void showOkCancelDialog(Context contexto,String titulo,String mensaje){
        AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
        alert.setTitle(titulo);
        alert.setMessage(mensaje);

// Set an EditText view to get user input
        final EditText input = new EditText(contexto);
        //input.setText("HHmmss");
        alert.setView(input);





        alert.show();

    }

}