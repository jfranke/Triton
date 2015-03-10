package cse190.triton;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class SoundFragment extends Fragment  implements View.OnClickListener{
    public static final String TAG = "SoundFragment";

    LinearLayout layout;
    LinearLayout mainLayout;


    public SoundFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sound, container, false);
        ImageButton soundOptions = (ImageButton) view.findViewById(R.id.soundButton);
        soundOptions.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.soundButton) {
            layout = new LinearLayout(v.getContext());
            mainLayout = new LinearLayout(v.getContext());
            SoundOptions.doPopUp(v.getContext(), v, layout, mainLayout);
        }
    }


}
