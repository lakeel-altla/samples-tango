package com.lakeel.altla.sample.tango.tangoux;

import com.google.atap.tango.ux.TangoUxLayout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;


public final class MainFragment extends Fragment {

    @BindView(R.id.layout_tango_ux)
    TangoUxLayout mTangoUxLayout;

    private TangoHost mTangoHost;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mTangoHost = TangoHost.class.cast(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        mTangoHost.getTangoUx().setLayout(mTangoUxLayout);

        return view;
    }
}
