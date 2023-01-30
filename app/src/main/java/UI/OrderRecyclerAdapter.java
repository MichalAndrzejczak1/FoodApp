package UI;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.BR;
import com.example.foodapp.R;
import com.example.foodapp.databinding.OrderRowBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.Order;

public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerAdapter.ViewHolder> {
   private Context context;
   private List<Order> orderList;
   public OrderRowBinding orderRowBinding;

    public OrderRecyclerAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        OrderRowBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.getContext()),
                R.layout.order_row, viewGroup, false);

        return new ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull OrderRecyclerAdapter.ViewHolder viewHolder, int position) {
        Order order = orderList.get(position);
                String timeAgo = (String) DateUtils.getRelativeTimeSpanString(order
                .getTimeAdded()
                .getSeconds() * 1000);
        orderRowBinding.setVariable(BR.timestamp, timeAgo);
        orderRowBinding.ivOrderImageList.setImageResource(context.getResources().getIdentifier(orderList.get(position).getPicture(),"drawable",context.getPackageName()));
        switch (position%5) {
            case 0: {
                orderRowBinding.cvMainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background1));
                break;
            }
            case 1: {
                orderRowBinding.cvMainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background2));
                break;
            }
            case 2: {
                orderRowBinding.cvMainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background3));
                break;
            }
            case 3: {
                orderRowBinding.cvMainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background4));
                break;
            }
            case 4: {
                orderRowBinding.cvMainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background5));
                break;
            }
        }
        viewHolder.bind(order);

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(OrderRowBinding orderRowBinding_) {
            super(orderRowBinding_.getRoot());
            orderRowBinding = orderRowBinding_;
        }

        public void bind(Object obj) {
            Order obj2 = (Order) obj;


            orderRowBinding.tvOrderTitle.setText(obj2.getTitle());
            orderRowBinding.tvOrderDescription.setText(obj2.getDescription());
            orderRowBinding.tvOrderCount.setText("Count: "+ obj2.getCount());
            orderRowBinding.tvOrderTotalPrice.setText("Total Price: "+ obj2.gettPrice());
//
//            orderRowBinding.setVariable(BR.model, obj);
//            orderRowBinding.tvOrderTitle.setText(obj2.getTitle());
            orderRowBinding.executePendingBindings();
        }
    }
}
