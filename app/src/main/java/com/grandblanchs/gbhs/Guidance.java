package com.grandblanchs.gbhs;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;



public class Guidance extends Fragment {

    public interface OnFragmentInteractionListener{}

    ProgressBar prog;
    ListView lst_guidance;

    String[] names;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guidance, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prog = (ProgressBar) view.findViewById(R.id.prog);
        lst_guidance = (ListView) view.findViewById(R.id.lst_guidance);

        names = getResources().getStringArray(R.array.guidance_names);

        ListAdapter guidanceAdapter = new OfficeAdapter
                (getActivity(), 1, names);

        lst_guidance.setAdapter(guidanceAdapter);

        FadeAnimation f = new FadeAnimation();
        f.start(lst_guidance, null, prog);
    }
}