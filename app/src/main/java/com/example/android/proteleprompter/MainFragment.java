package com.example.android.proteleprompter;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.proteleprompter.Adaptor.DocumentAdaptor;
import com.example.android.proteleprompter.ContentProvider.DocumentContract;
import com.example.android.proteleprompter.Data.Document;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DocumentAdaptor mAdaptor;

    //private NewDocumentAdaptor mAdapter2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<Document> mDocumentList;

    private TextView mTextView_listSubtitle;

    private static final String TAG = "MainActivityFragment";

    private RecyclerView mDocument_list_rv;

    private final String BUNDLE_RECYCLE_LAYOUT = "recycler_view_bundle";

    private LinearLayoutManager mLinearLayoutManager;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDocumentList = new ArrayList<>();
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        mDocument_list_rv = root.findViewById(R.id.rv_list);

        mTextView_listSubtitle = root.findViewById(R.id.tv_list_subtitle);

        mTextView_listSubtitle.setText("Today");

        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpListOfDocumentListView(mDocument_list_rv);

        getLoaderManager().initLoader(0, null, this);

    }

    private void setUpListOfDocumentListView(RecyclerView documentListView) {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mAdaptor = new DocumentAdaptor(getActivity(), null);
        mAdaptor.setHasStableIds(true);
        documentListView.setAdapter(mAdaptor);
        documentListView.setLayoutManager(mLinearLayoutManager);

    }

    public void updateAdapter() {
        getLoaderManager().destroyLoader(0);
        getLoaderManager().restartLoader(0, null, this);
    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                return new CursorLoader(getActivity(), DocumentContract.DocumentEntry.CONTENT_URI, null, null, null, null);
            default:
                throw new IllegalArgumentException("no id handled!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case 0:

                Cursor cursor = ((DocumentAdaptor) mDocument_list_rv.getAdapter()).getCursor();

                //fill all exisitng in adapter
                MatrixCursor mx = new MatrixCursor(DocumentContract.DocumentEntry.Columns);
                fillMx(cursor, mx);

                //fill with additional result
                fillMx(data, mx);

                ((DocumentAdaptor) mDocument_list_rv.getAdapter()).swapCursor(mx);

                handlerToWait.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    }
                }, 2000);

                break;
            default:
                throw new IllegalArgumentException("no loader id handled!");
        }
    }

    private Handler handlerToWait = new Handler();

    private void fillMx(Cursor data, MatrixCursor mx) {
        if (data == null)
            return;

        data.moveToPosition(-1);
        while (data.moveToNext()) {
            mx.addRow(new Object[]{
                    data.getString(data.getColumnIndex(DocumentContract.DocumentEntry._ID)),
                    data.getString(data.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_NAME)),
                    data.getString(data.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_TYPE)),
                    data.getString(data.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_LASTOPENTIME)),
                    data.getString(data.getColumnIndex(DocumentContract.DocumentEntry.COLUMN_DOCUMENT_CONTENT))
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdaptor.swapCursor(null);
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLE_LAYOUT);
            mDocument_list_rv.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLE_LAYOUT, mDocument_list_rv.getLayoutManager().onSaveInstanceState());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
