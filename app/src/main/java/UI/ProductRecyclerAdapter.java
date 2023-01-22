package UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp.R;
import com.example.foodapp.databinding.CategoryBinding;
import com.example.foodapp.databinding.ProductBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.Category;
import Model.Product;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Product> productList;
    public ProductBinding productBinding;

    private static ProductRecyclerAdapter.ClickListener clickListener;


    public ProductRecyclerAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public ProductRecyclerAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product,viewGroup,false);

        return new ViewHolder(inflate);

    }

    @Override
    public void onBindViewHolder(@NonNull ProductRecyclerAdapter.ViewHolder viewHolder, int position) {
        viewHolder.productName.setText(productList.get(position).getTitle());
        viewHolder.price.setText(productList.get(position).getPrice().toString());
        viewHolder.productPic.setImageResource(context.getResources().getIdentifier(productList.get(position).getPicture(),"drawable",context.getPackageName()));

        switch (position%5){
            case 0: {
                viewHolder.mainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background1));
                break;
            }
            case 1: {

                viewHolder.mainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background2));
                break;
            }
            case 2: {
                viewHolder.mainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background3));
                break;
            }
            case 3: {
                viewHolder.mainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background4));
                break;
            }
            case 4: {
//                int drawableId = context.getResources().getIdentifier(productList.get(4).getPicture(), "drawable", context.getPackageName());
//                Picasso.get()
//                        .load(drawableId)
//                        .placeholder(R.drawable.image_three)
//                        .fit()
//                        .into(viewHolder.productPic);
                viewHolder.mainLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.cat_background5));
                break;
            }

        }


        Product product = productList.get(position);
        viewHolder.bind(product);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnLongClickListener,View.OnClickListener {
        TextView productName;
        TextView price;
        ImageView productPic;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tvTitleProduct);
            productPic = itemView.findViewById(R.id.ivProduct);
            price = itemView.findViewById(R.id.tvPrice);
            mainLayout = itemView.findViewById(R.id.mainLayout);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Object obj) {
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return false;
        }
    }

    public void setOnItemClickListener(ProductRecyclerAdapter.ClickListener clickListener) {
        ProductRecyclerAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);

    }
}
