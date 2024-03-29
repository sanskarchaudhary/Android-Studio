package com.cometchat.pro.uikit.ui_components.users.block_users;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.core.BlockedUsersRequest;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.ui_components.shared.CometChatSnackBar;
import com.cometchat.pro.uikit.ui_resources.utils.CometChatError;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cometchat.pro.uikit.ui_resources.utils.recycler_touch.ClickListener;
import com.cometchat.pro.uikit.ui_resources.utils.recycler_touch.RecyclerTouchListener;
import com.cometchat.pro.uikit.ui_resources.utils.FontUtils;
import com.cometchat.pro.uikit.ui_resources.utils.Utils;

/**
 * Purpose - CometChatBlockUserListScreen.class is a screen used to display List of blocked users.
 * It also helps to perform action like unblock user.
 *
 * Created on - 20th December 2019
 *
 * Modified on  - 16th January 2020
 *
 */


public class CometChatBlockUserList extends Fragment {
    private static final String TAG = "CometChatGroupMember";

    private int LIMIT = 100;

    private BlockedListAdapter blockedUserAdapter;

    private BlockedUsersRequest blockedUserRequest;

    private RecyclerView rvUserList;

    private FontUtils fontUtils;

    private TextView noBlockUserLayout;

    private List<User> userList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fontUtils=FontUtils.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cometchat_block_user, container, false);
        setHasOptionsMenu(true);
        rvUserList = view.findViewById(R.id.rv_blocked_user_list);
        noBlockUserLayout = view.findViewById(R.id.no_block_user);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_blocked_user);
        setToolbar(toolbar);

        CometChatError.init(getContext());
        rvUserList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (!recyclerView.canScrollVertically(1)) {
                    fetchBlockedUser();
                }

            }
        });

        // It unblock users when click on item in rvUserList
        rvUserList.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rvUserList, new ClickListener() {

            @Override
            public void onClick(View var1, int var2) {
                User user = (User)var1.getTag(R.string.user);
                if (getActivity()!=null) {
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(getActivity());
                    alert.setTitle(getResources().getString(R.string.unblock));
                    String message = String.format(getResources().getString(R.string.unblock_user_question),user.getName());
                    alert.setMessage(message);
                    alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            unBlockUser(user, var1);
                        }
                    });
                    alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alert.create();
                    alert.show();
                }
            }
        }));

        fetchBlockedUser();

        return view;
    }

    private void setToolbar(MaterialToolbar toolbar) {
        if (getActivity()!=null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (Utils.changeToolbarFont(toolbar)!=null){
            Utils.changeToolbarFont(toolbar).setTypeface(fontUtils.getTypeFace(FontUtils.robotoMedium));
        }
    }

    private void unBlockUser(User user, View var1) {

        ProgressDialog progressDialog = ProgressDialog.show(getContext(),null,
                user.getName()+" "+getResources().getString(R.string.unblocked_successfully));
        ArrayList<String> uids = new ArrayList<>();
        uids.add(user.getUid());
        CometChat.unblockUsers(uids, new CometChat.CallbackListener<HashMap<String, String>>() {
            @Override
            public void onSuccess(HashMap<String, String> stringStringHashMap) {
                if (userList.contains(user))
                    userList.remove(user);
                blockedUserAdapter.removeUser(user);
                progressDialog.dismiss();
//                CometChatSnackBar.show(getContext(),var1,
//                        ,CometChatSnackBar.SUCCESS);
                checkIfNoUserVisible();
            }

            @Override
            public void onError(CometChatException e) {
                progressDialog.dismiss();
                CometChatSnackBar.show(getContext(),var1,
                        CometChatError.localized(e),CometChatSnackBar.ERROR);
                Log.e(TAG, "onError: "+e.getMessage());
            }
        });
    }

    /**
     * This method is used to fetch list of blocked users.
     *
     * @see BlockedUsersRequest
     */
    private void fetchBlockedUser() {
        if (blockedUserRequest == null) {
            blockedUserRequest = new BlockedUsersRequest.BlockedUsersRequestBuilder().setDirection(BlockedUsersRequest.DIRECTION_BLOCKED_BY_ME).setLimit(LIMIT).build();
        }
        blockedUserRequest.fetchNext(new CometChat.CallbackListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                userList.addAll(users);
                if (users.size() > 0) {
                    setAdapter(users);
                }
                checkIfNoUserVisible();
            }

            @Override
            public void onError(CometChatException e) {
                Log.e(TAG, "onError: "+e.getMessage());
                CometChatSnackBar.show(getContext(),
                        rvUserList,getResources().getString(R.string.block_user_list_error)+", "+
                                CometChatError.localized(e), CometChatSnackBar.ERROR);
            }
        });
    }

    private void checkIfNoUserVisible() {
        if (userList.size()==0) {
            noBlockUserLayout.setVisibility(View.VISIBLE);
            rvUserList.setVisibility(View.GONE);
        } else {
            noBlockUserLayout.setVisibility(View.GONE);
            rvUserList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            if (getActivity() != null)
                getActivity().onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is used to set Adapter for rvUserlist to display blocked users.
     *
     * @param users
     */
    private void setAdapter(List<User> users) {
        if (blockedUserAdapter==null){
            blockedUserAdapter=new BlockedListAdapter(getContext(),users);
            rvUserList.setAdapter(blockedUserAdapter);
        }else {
            blockedUserAdapter.updateList(Utils.userSort(users));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
