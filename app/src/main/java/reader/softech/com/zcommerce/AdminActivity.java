package reader.softech.com.zcommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import reader.softech.com.zcommerce.Model.AdminOrders;
import reader.softech.com.zcommerce.Model.Cart;
import reader.softech.com.zcommerce.Prevelant.AdminPrevelant;
import reader.softech.com.zcommerce.Prevelant.Prevelant;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference ordersRef,cartRef;
    private String userId="";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        userId=getIntent().getStringExtra("uid");
       // userId=AdminPrevelant.currentOnlineAdmin.getPhone();

        ordersRef= FirebaseDatabase.getInstance().getReference().child("Orders");
        cartRef= FirebaseDatabase.getInstance().getReference()
                .child("Cart List").child("Admin View");


        orderList=findViewById(R.id.orders_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<AdminOrders> options=
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersRef,AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter=
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrders model) {

                        String checker = getIntent().getStringExtra("adminPhoneNumber");

                        if (Prevelant.currentOnlineUser.getPhone() == checker){
                            holder.userName.setText("Name: " + model.getName().toString());
                        holder.userPhoneNumber.setText("Phone: " + model.getPhone().toString());
                        holder.userTotalPrice.setText("Total Amount= $" + model.getTotalAmount().toString());
                        holder.userDateTime.setText("Ordered at: " + model.getDate() + " " + model.getTime().toString());
                        holder.userShippingAddress.setText("Shipping address: " + model.getAddress() + ", " + model.getCity().toString());

                        holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String uID = getRef(position).getKey();
                                String sellerNumber = getRef(position).getKey();
                                Intent intent = new Intent(AdminActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("uid", uID);
                                intent.putExtra("adminPhoneNumber", sellerNumber);
                                startActivity(intent);

                            }
                        });
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[] = new CharSequence[]
                                        {

                                                "Yes",
                                                "No"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                                builder.setTitle("Have you shipped this order products?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            String uID = getRef(position).getKey();
                                            String userId = getRef(position).getKey();
                                            RemoveOrder(uID);
                                            RemoveProduct(userId);

                                        } else {
                                            finish();
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });

                    }
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };



        orderList.setAdapter(adapter);
        adapter.startListening();
    }



    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder
    {

        public TextView userName,userPhoneNumber,userTotalPrice,userDateTime,userShippingAddress;
        public Button showOrdersBtn;


        public AdminOrdersViewHolder(View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.user_name);
            userPhoneNumber=itemView.findViewById(R.id.user_phone_number);
            userTotalPrice=itemView.findViewById(R.id.order_price);
            userDateTime=itemView.findViewById(R.id.order_date_time);
            userShippingAddress=itemView.findViewById(R.id.address_city);
            showOrdersBtn=itemView.findViewById(R.id.show_all_products_btn);



        }
    }
    private void RemoveOrder(String uID)
    {
        ordersRef.child(uID).removeValue();


    }

    private void RemoveProduct(String userId)
    {

        cartRef.child(userId).removeValue();
    }
}

//open ME
