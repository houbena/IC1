package houzone.net.weatherapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;


/**
 * Created by houssein on 27/06/15.
 */
public class AlertDialogFragment extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_title))
                .setMessage(context.getString(R.string.dialog_message))
                .setPositiveButton(context.getString(R.string.dialog_button), null);
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
