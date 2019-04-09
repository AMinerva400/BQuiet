package minerva.anthony.bquiet;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText, nameText;
    //ImageView profileImage;

    ReceivedMessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.tvMessageBody);
        timeText = itemView.findViewById(R.id.tvMessageTime);
        nameText = itemView.findViewById(R.id.tvMessageName);
        //profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
    }

    void bind(Message message, Context context) {
        messageText.setText(message.getMessage());

        // Format the stored timestamp into a readable String using method.
        timeText.setText(DateUtils.formatDateTime(context, message.getCreatedAt(), 0));
        nameText.setText(message.getSender().getName());

        // Insert the profile image from the URL into the ImageView.
        //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
    }
}
