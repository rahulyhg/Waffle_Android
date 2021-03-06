package com.pipit.waffle.Objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pipit.waffle.Constants;
import com.pipit.waffle.R;
import com.pipit.waffle.ToolbarActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Eric on 12/7/2014.
 */
public class Question {

    private String questionBody;
    private String id;
    private List<Choice> choices;
    private User asker;
    public QuestionState state=QuestionState.NOT_LOADED;

    private QuestionType type;
    public int imagesLoaded = 0;

    public Question(String qbody, User asker){
        setQuestionBody(qbody);
        choices = new ArrayList<Choice>();
        setAsker(asker);
    }

    public void addChoice(Choice ans){
        //ans.setQuestionID(questionID);
        if (ans.getUrl() == null || ans.getUrl().equals("")){
            //No image
            ans.imageState = Choice.LoadState.NO_IMAGE;
        }
        else{
            ans.imageState = Choice.LoadState.NOT_LOADED;
        }
        this.choices.add(ans);
    }

    public boolean beginImageLoading(){
        if (choices.size()!= Constants.NUMBER_OF_CHOICES_PER_QUESTION){
            return false;
        }
        for (int i = 0 ; i < choices.size(); i++) {
            if (choices.get(i).imageState != Choice.LoadState.NO_IMAGE) {
                loadURLintoBitmap(choices.get(i), choices.get(i).getUrl());
            }else{
                loadTextOnlyBackground(choices.get(i));
            }
        }
        //type = QuestionType.PICTURE; //Todo: consider the possibility of caption
        return true;
    }

    public void deleteAnswer(Choice ans){
        this.choices.remove(ans);
    }
    /**
     * @return the questionBody
     */
    public String getQuestionBody() {
        return questionBody;
    }

    /**
     * @param questionBody the questionBody to set
     */
    public void setQuestionBody(String questionBody) {
        this.questionBody = questionBody;
    }
    /**
     * @return the asker
     */
    public User getAsker() {
        return asker;
    }

    /**
     * @param asker the asker to set
     */
    public void setAsker(User asker) {
        this.asker = asker;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public enum QuestionType {
        TEXT, PICTURE, CAPTIONED
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void generateAndSetID(){
        this.id = UUID.randomUUID().toString();
    }


    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public enum QuestionState{
        LOADED, NOT_LOADED, FAILED;
    }

    /**
     * For all text-only choices, load a blank placeholder image in background
     * Currently we set the imagestate to IMAGE_READY when this happens, although this is
     * a bit of a possible misnomer, as NO_IMAGE is still a more accurate status. For now,
     * this allows us to pass the image through.
     * @return True if successful
     */
    public boolean loadTextOnlyBackground(final Choice ans){
        try {
            ans.set_image(BitmapFactory.decodeResource(ToolbarActivity.getMcontext().getResources(), R.drawable.chelsealogo));
            ans.imageState = Choice.LoadState.IMAGE_READY;

            boolean isReady = ClientData.checkAndUpdateQuestionStatus(getInstance());
            Log.d("Question", "Placeholder image loaded - Question ready to use = " + Boolean.toString(isReady));
        }catch (Exception e){
            ans.imageState = Choice.LoadState.FAILED;
            state = QuestionState.FAILED;
            ClientData.tipoffSecretPolice();
            return false;
        }
        return true;
    }

    public boolean loadURLintoBitmap(final Choice ans, String imageUri){
        ImageSize targetSize = new ImageSize(ClientData.getCardWidth(), ClientData.getCardHeight());
        ans.imageLoader.loadImage(imageUri, targetSize, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ans.imageState = Choice.LoadState.NOT_LOADED;
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ans.set_image(loadedImage);
                ans.imageState = Choice.LoadState.IMAGE_READY;

                boolean isReady = ClientData.checkAndUpdateQuestionStatus(getInstance());
                Log.d("Question", "Image Loading Completed - Question ready to use = " + " and size is "
                        + Boolean.toString(isReady)
                        + loadedImage.getRowBytes()*loadedImage.getHeight());
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                ans.imageState = Choice.LoadState.FAILED;
                state = QuestionState.FAILED;
                ClientData.tipoffSecretPolice();
            }
        });
        return true;
    }

    public Question getInstance(){
        return this;
    }


}