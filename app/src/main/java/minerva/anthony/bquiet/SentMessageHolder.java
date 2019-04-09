package minerva.anthony.bquiet;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class SentMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText;
    //ImageView profileImage;

    SentMessageHolder(View itemView) {
        super(itemView);
        messageText = itemView.findViewById(R.id.tvMessageBody);
        timeText = itemView.findViewById(R.id.tvMessageTime);
    }

    void bind(Message message, Context context) {
        messageText.setText(message.getMessage());
        // Format the stored timestamp into a readable String using DateUtils method formatDateTime.
        timeText.setText(DateUtils.formatDateTime(context, message.getCreatedAt(), 0));
    }
}
