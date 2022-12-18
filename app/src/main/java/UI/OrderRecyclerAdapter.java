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

            int activeCategoryNumber = obj2.getCategoryNumber();
            int activeProductNumber = obj2.getProductNumber();
            switch (activeCategoryNumber) {
                case 0:{
                    //pizzas
                    switch (activeProductNumber){
                        case 0:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.pizza1));
                            break;
                        }
                        case 1:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.pizza2));
                            break;
                        }
                        case 2:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.pizza3));
                            break;
                        }
                        case 3:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.pizza4));
                            break;
                        }
                        case 4:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.pizza5));
                            break;
                        }
                    }
                    break;
                }
                case 1:{
                    //burgers
                    switch (activeProductNumber){
                        case 0:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.burger1));
                            break;
                        }
                        case 1:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.burger2));
                            break;
                        }
                        case 2:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.burger3));
                            break;
                        }
                        case 3:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.burger4));
                            break;
                        }
                        case 4:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.burger5));
                            break;
                        }
                    }
                    break;
                }
                case 2:{
                    //hotdogs
                    switch (activeProductNumber){
                        case 0:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.hotdog1));
                            break;
                        }
                        case 1:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.hotdog2));
                            break;
                        }
                        case 2:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.hotdog3));
                            break;
                        }
                    }
                    break;
                }
                case 3:{
                    //drinks
                    switch (activeProductNumber){
                        case 0:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.drink1));
                            break;
                        }
                        case 1:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.drink2));
                            break;
                        }
                        case 2:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.drink3));
                            break;
                        }
                        case 3:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.drink4));
                            break;
                        }
                        case 4:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.drink5));
                            break;
                        }
                    }
                    break;
                }
                case 4:{
                    //donuts
                    switch (activeProductNumber){
                        case 0:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.donut1));
                            break;
                        }
                        case 1:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.donut2));
                            break;
                        }
                        case 2:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.donut3));
                            break;
                        }
                        case 3:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.donut4));
                            break;
                        }
                        case 4:{
                            orderRowBinding.ivOrderImageList.setImageDrawable(context.getResources().getDrawable(R.drawable.donut5));
                            break;
                        }
                    }
                    break;
                }
            }


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
