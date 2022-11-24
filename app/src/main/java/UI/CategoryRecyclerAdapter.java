package UI;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.BR;
import com.example.foodapp.R;
import com.example.foodapp.databinding.OrderRowBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.Order;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Order> orderList;
    public OrderRowBinding orderRowBinding;

    public CategoryRecyclerAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public CategoryRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        OrderRowBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.getContext()),
                R.layout.category, viewGroup, false);

        return new ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull CategoryRecyclerAdapter.ViewHolder viewHolder, int position) {
        Order order = orderList.get(position);
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(order
                .getTimeAdded()
                .getSeconds() * 1000);
        orderRowBinding.setVariable(BR.timestamp, timeAgo);



        viewHolder.bind(order);

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(OrderRowBinding orderRowBinding_) {
            super(orderRowBinding_.getRoot());
            orderRowBinding = orderRowBinding_;
        }

        public void bind(Object obj) {
            Order obj2 = (Order) obj;

            ImageView iv = new ImageView(context);
            Picasso.get()
                    .load(obj2.getImageURL())
                    .placeholder(R.drawable.image_three)
                    .fit()
                    .into(orderRowBinding.ivOrderImageList);

//            journalRowBinding.setVariable(BR.url, iv);

            orderRowBinding.setVariable(BR.model, obj);
            orderRowBinding.tvOrderTitle.setText(obj2.getTitle());
            orderRowBinding.executePendingBindings();
        }
    }
}
