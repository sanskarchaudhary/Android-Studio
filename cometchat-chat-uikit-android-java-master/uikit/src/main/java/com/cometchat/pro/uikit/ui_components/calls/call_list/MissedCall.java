package com.cometchat.pro.uikit.ui_components.calls.call_list;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.shared.CometChatSnackBar;
import com.cometchat.pro.uikit.ui_components.shared.cometchatCalls.CometChatCalls;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.ui_resources.utils.CometChatError;
import com.cometchat.pro.uikit.ui_resources.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.cometchat.pro.uikit.ui_resources.utils.item_clickListener.OnItemClickListener;
import com.cometchat.pro.uikit.ui_components.groups.group_details.CometChatGroupDetailActivity;
import com.cometchat.pro.uikit.ui_components.users.user_details.CometChatUserDetailScreenActivity;
import com.cometchat.pro.uikit.ui_resources.utils.CallUtils;

/**
 * MissedCall.class is a Fragment class used to display all the call that has been missed by loggedIn
 * user.
 *
 * Created At : 25th March 2020
 *
 * Modified On : 02nd April 2020
 */
public class MissedCall extends Fragment {

    private CometChatCalls rvCallList;

    private LinearLayout noCallView;

    private MessagesRequest messagesRequest;

    private LinearLayoutManager linearLayoutManager;

    private String loggedInUid = CometChat.getLoggedInUser().getUid();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cometchat_missed_call, container, false);
        rvCallList = view.findViewById(R.id.callList_rv);
        CometChatError.init(getContext());
        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rvCallList.setLayoutManager(linearLayoutManager);
        noCallView = view.findViewById(R.id.no_call_vw);
        rvCallList.setItemClickListener(new OnItemClickListener<Call>() {
            @Override
            public void OnItemClick(Call var, int position) {
                if (var.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                    User user;
                    if (((User)var.getCallInitiator()).getUid().equals(CometChat.getLoggedInUser().getUid())) {
                        user =  ((User)var.getCallReceiver());
                    }
                    else
                    {
                        user = (User)var.getCallInitiator();
                    }
                    Intent intent = new Intent(getContext(), CometChatUserDetailScreenActivity.class);
                    intent.putExtra(UIKitConstants.IntentStrings.UID, user.getUid());
                    intent.putExtra(UIKitConstants.IntentStrings.NAME, user.getName());
                    intent.putExtra(UIKitConstants.IntentStrings.AVATAR, user.getAvatar());
                    intent.putExtra(UIKitConstants.IntentStrings.LINK,user.getLink());
                    intent.putExtra(UIKitConstants.IntentStrings.STATUS, user.getStatus());
                    intent.putExtra(UIKitConstants.IntentStrings.IS_BLOCKED_BY_ME, user.isBlockedByMe());
                    intent.putExtra(UIKitConstants.IntentStrings.FROM_CALL_LIST,true);
                    startActivity(intent);
                }
                else {
                    Group group;
                    group = ((Group)var.getCallReceiver());
                    Intent intent = new Intent(getContext(), CometChatGroupDetailActivity.class);
                    intent.putExtra(UIKitConstants.IntentStrings.GUID, group.getGuid());
                    intent.putExtra(UIKitConstants.IntentStrings.NAME, group.getName());
                    intent.putExtra(UIKitConstants.IntentStrings.AVATAR, group.getIcon());
                    intent.putExtra(UIKitConstants.IntentStrings.MEMBER_SCOPE, group.getScope());
                    intent.putExtra(UIKitConstants.IntentStrings.GROUP_OWNER, group.getOwner());
                    intent.putExtra(UIKitConstants.IntentStrings.GROUP_TYPE,group.getGroupType());
                    intent.putExtra(UIKitConstants.IntentStrings.MEMBER_COUNT,group.getMembersCount());
                    intent.putExtra(UIKitConstants.IntentStrings.GROUP_PASSWORD,group.getPassword());
                    intent.putExtra(UIKitConstants.IntentStrings.GROUP_DESC,group.getDescription());
                    startActivity(intent);
                }
            }
        });

        rvCallList.setItemCallClickListener(new OnItemClickListener<Call>() {
            @Override
            public void OnItemClick(Call var, int position) {
                checkOnGoingCall(var);
            }
        });
        rvCallList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!rvCallList.canScrollVertically(1))
                {
                    getCallList();
                }
            }
        });
        return view;
    }

    private void checkOnGoingCall(Call var) {
        if(CometChat.getActiveCall()!=null && CometChat.getActiveCall().getCallStatus().equals(CometChatConstants.CALL_STATUS_ONGOING) && CometChat.getActiveCall().getSessionId()!=null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(getContext().getResources().getString(R.string.ongoing_call))
                    .setMessage(getContext().getResources().getString(R.string.ongoing_call_message))
                    .setPositiveButton(getContext().getResources().getString(R.string.join), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CallUtils.joinOnGoingCall(getContext(),CometChat.getActiveCall());
                        }
                    }).setNegativeButton(getContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
        else {
            initiateCall(var);
        }
    }

    private void initiateCall(Call var) {
        CometChat.initiateCall(var, new CometChat.CallbackListener<Call>() {
            @Override
            public void onSuccess(Call call) {
                Log.e("onSuccess: ", call.toString());
                if (call.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                    User user;
                    if (((User) call.getCallInitiator()).getUid().equals(CometChat.getLoggedInUser().getUid())) {
                        user = ((User) call.getCallReceiver());
                    } else {
                        user = (User) call.getCallInitiator();
                    }
                    CallUtils.startCallIntent(getContext(), user, CometChatConstants.CALL_TYPE_AUDIO, true, call.getSessionId());
                } else
                    CallUtils.startGroupCallIntent(getContext(), ((Group) call.getCallReceiver()), CometChatConstants.CALL_TYPE_AUDIO, true, call.getSessionId());
            }

            @Override
            public void onError(CometChatException e) {
                if (rvCallList != null)
                    CometChatSnackBar.show(getContext(),rvCallList,CometChatError.localized(e)
                            ,CometChatSnackBar.ERROR);
            }
        });
    }

    private void getCallList() {
        if (messagesRequest == null)
        {
            messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                    .setCategories(Arrays.asList(CometChatConstants.CATEGORY_CALL)).build();
        }

        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> baseMessages) {
                Collections.reverse(baseMessages);
                rvCallList.setCallList(filteredMissedCall(baseMessages));
                if (rvCallList.size()!=0)
                {
                    noCallView.setVisibility(View.GONE);
                }
                else
                    noCallView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(CometChatException e) {
                if (rvCallList!=null)
                    CometChatSnackBar.show(getContext(),rvCallList,
                            CometChatError.localized(e),CometChatSnackBar.ERROR);
            }
        });
    }

    private List<BaseMessage> filteredMissedCall(List<BaseMessage> baseMessages) {
        List<BaseMessage> filteredList = new ArrayList<>();
        for (BaseMessage baseMessage : baseMessages) {
            Call call = (Call)baseMessage;
            if(!((User)call.getCallInitiator()).getUid().equals(loggedInUid)) {
                if (((Call) baseMessage).getCallStatus().equals(CometChatConstants.CALL_STATUS_UNANSWERED)
                        || ((Call) baseMessage).getCallStatus().equals(CometChatConstants.CALL_STATUS_CANCELLED)) {
                    filteredList.add(baseMessage);
                }
            }
        }
        return filteredList;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible)
        {
            getCallList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
