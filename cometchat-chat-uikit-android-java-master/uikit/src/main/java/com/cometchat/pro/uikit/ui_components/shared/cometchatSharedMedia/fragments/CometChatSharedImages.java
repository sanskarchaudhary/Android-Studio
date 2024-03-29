package com.cometchat.pro.uikit.ui_components.shared.cometchatSharedMedia.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.uikit.ui_components.shared.CometChatSnackBar;
import com.cometchat.pro.uikit.ui_resources.utils.CometChatError;
import com.cometchat.pro.uikit.ui_resources.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.cometchat.pro.uikit.ui_components.shared.cometchatSharedMedia.adapter.CometChatSharedMediaAdapter;

/**
 * Purpose - It is a Fragment which is used in SharedMedia to display Images sent or received to a
 * particular receiver.
 *
 * Created On - 20th March 2020
 *
 * Modified On - 23rd March 2020
 *
 */
public class CometChatSharedImages extends Fragment {

    private RecyclerView rvFiles;
    private CometChatSharedMediaAdapter adapter;
    private String Id;
    private String type;
    private List<BaseMessage> messageList = new ArrayList<>();
    private LinearLayout noMedia;
    private MessagesRequest messagesRequest;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cometchat_shared_media,container,false);
        rvFiles = view.findViewById(R.id.rvFiles);
        rvFiles.setLayoutManager(new GridLayoutManager(getContext(),2));
        noMedia = view.findViewById(R.id.no_media_available);
        Id = this.getArguments().getString("Id");
        type = this.getArguments().getString("type");
        fetchMessage();

        rvFiles.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1)) {
                    fetchMessage();
                }

            }
        });
        return view;
    }

    /**
     * This method is used to fetch a messages whose type is MESSAGE_TYPE_IMAGE.
     * @see MessagesRequest#fetchNext(CometChat.CallbackListener)
     */
    private void fetchMessage() {
        if (messagesRequest==null)
        {
            if (type!=null && type.equals(CometChatConstants.RECEIVER_TYPE_USER))
                messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                        .setCategories(Arrays.asList(CometChatConstants.CATEGORY_MESSAGE))
                        .setTypes(Arrays.asList(CometChatConstants.MESSAGE_TYPE_IMAGE))
                        .setUID(Id).setLimit(30).build();
            else
                messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                        .setCategories(Arrays.asList(CometChatConstants.CATEGORY_MESSAGE))
                        .setTypes(Arrays.asList(CometChatConstants.MESSAGE_TYPE_IMAGE))
                        .setGUID(Id).setLimit(30).build();
        }
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> baseMessageList) {
                if (baseMessageList.size()!=0)
                    setImageAdapter(baseMessageList);
                checkMediaVisble();
            }

            @Override
            public void onError(CometChatException e) {
                Log.e( "onError: ",e.getMessage() );
                if (rvFiles!=null)
                    CometChatSnackBar.show(getContext(),rvFiles, CometChatError.localized(e), CometChatSnackBar.ERROR);
            }
        });
    }

    /**
     * This method is used to check if the size of messages fetched is greater than 0 or not. If it
     * is 0 than it will show "No media Available" message.
     */
    private void checkMediaVisble() {
        if (messageList.size() != 0) {
            rvFiles.setVisibility(View.VISIBLE);
            noMedia.setVisibility(View.GONE);
        } else {
            noMedia.setVisibility(View.VISIBLE);
            rvFiles.setVisibility(View.GONE);
        }
    }

    /**
     * This method is used to setAdapter for Image messages.
     * @param baseMessageList is object of List<BaseMessage> which contains list of messages.
     * @see CometChatSharedMediaAdapter
     */
    private void setImageAdapter(List<BaseMessage> baseMessageList) {

        List<BaseMessage> filteredList = removeDeletedMessage(baseMessageList);
        messageList.addAll(filteredList);
        if (adapter==null)
        {
            adapter = new CometChatSharedMediaAdapter(getContext(),filteredList);
            rvFiles.setAdapter(adapter);
        }
        else
            adapter.updateMessageList(filteredList);
    }

    /**
     * This method is used to remove deleted messages from message list and return filteredlist.
     * (baseMessage whose deletedAt !=0 must be removed from message list)
     *
     * @param baseMessageList is object of List<BaseMessage>
     * @return filteredMessageList which does not have deleted messages.
     * @see BaseMessage
     */
    private List<BaseMessage> removeDeletedMessage(List<BaseMessage> baseMessageList) {
        List<BaseMessage> resultList = new ArrayList<>();
        for (BaseMessage baseMessage : baseMessageList)
        {
            if(baseMessage.getDeletedAt()==0)
            {
                resultList.add(baseMessage);
            }
        }
        return resultList;
    }
}
