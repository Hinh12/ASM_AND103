package com.example.asm_ph42207;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.Viewholer> {
    List<CarModel> carModelList;
    Context context;

    public interface OnDeleteClickListener {
        void onDeleteClick(int pos);
    }
    private OnDeleteClickListener onDeleteClickListener;

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }
    private OnEditClickListener onEditClickListener;

    public interface OnEditClickListener {
        void onEditClick(int pos);
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.onEditClickListener = listener;
    }

    public CarAdapter(Context context, List<CarModel> carModelList) {
        this.context = context;
        this.carModelList = carModelList;

    }

    @NonNull
    @Override
    public Viewholer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);
        return new Viewholer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholer holder, int position) {

        CarModel car = carModelList.get(position);

        holder.tvName.setText("Tên xe: " + car.getTen());
        holder.tvNamSX.setText("Năm sản xuất xe: " + String.valueOf(car.getNamSX()));
        holder.tvHang.setText("Hãng xe: " + car.getHang());
        holder.tvGia.setText("Giá xe: " + String.valueOf(car.getGia()));
        holder.imgbtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(position);

            }
        });

        holder.imgbtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onEditClickListener != null) {
                    onEditClickListener.onEditClick(holder.getAdapterPosition());
                }
//                opendialogsua(car);
            }
        });


    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        if (carModelList != null) {
            return carModelList.size();
        }
        return 0;
    }


    public class Viewholer extends RecyclerView.ViewHolder {
        TextView tvName, tvNamSX, tvHang, tvGia;
        ImageButton imgbtnUpdate, imgbtnDelete;

        public Viewholer(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvNamSX = itemView.findViewById(R.id.tvNamSX);
            tvHang = itemView.findViewById(R.id.tvHang);
            tvGia = itemView.findViewById(R.id.tvGia);
            imgbtnDelete = itemView.findViewById(R.id.img_btn_delete);
            imgbtnUpdate = itemView.findViewById(R.id.img_btn_edit);

        }
    }

    public void deleteItem(int position) {
        CarModel carModel = carModelList.get(position);

        // Gọi API để xóa mục từ server
        APIService apiService = RetrofitClient.getClient().create(APIService.class);
        Call<Void> call = apiService.deleteCar(carModel.get_id());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Xóa mục từ danh sách
                    carModelList.remove(position);
                    // Cập nhật ListView
                    notifyDataSetChanged();
                    // Hiển thị thông báo xóa thành công
                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Hiển thị thông báo lỗi nếu xóa không thành công
                    Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Hiển thị thông báo lỗi nếu gặp lỗi khi gọi API
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void opendialogsua(CarModel carModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context); // Thêm dòng này
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_update_car, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        TextInputEditText edtname = view.findViewById(R.id.edt_ten_xe);
        TextInputEditText edtnamSX = view.findViewById(R.id.edt_namSX_xe);
        TextInputEditText edthang = view.findViewById(R.id.edt_hang_xe);
        TextInputEditText edtgia = view.findViewById(R.id.edt_gia_xe);
        Button btnadd = view.findViewById(R.id.btn_Huy_Update_SP);
        edtname.setText(carModel.getTen());
        edtnamSX.setText(String.valueOf(carModel.getNamSX()));
        edthang.setText(String.valueOf(carModel.getHang()));
        edtgia.setText(String.valueOf(carModel.getGia()));
        btnadd.setText("Cập nhật");
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenSP = edtname.getText().toString();
                int namSX = Integer.parseInt(edtnamSX.getText().toString());
                String hang = edthang.getText().toString();
                double gia = Double.parseDouble(edtgia.getText().toString());


                Log.e("loi", tenSP + namSX + hang + gia );
                carModel.setTen(tenSP);
                carModel.setNamSX(namSX);
                carModel.setHang(hang);
                carModel.setGia(gia);


                // Gọi API để cập nhật sản phẩm
                APIService apiService = RetrofitClient.getClient().create(APIService.class);
                Call<CarModel> call = apiService.updateCar(carModel.get_id(), carModel);
                call.enqueue(new Callback<CarModel>() {
                    @Override
                    public void onResponse(Call<CarModel> call, Response<CarModel> response) {
                        if (response.isSuccessful()) {
                            // Cập nhật sản phẩm thành công
                            notifyDataSetChanged(); // Cập nhật RecyclerView
                            Toast.makeText(context, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss(); // Đóng dialog sau khi cập nhật thành công
                        } else {
                            // Xử lý lỗi nếu yêu cầu không thành công
                            Toast.makeText(context, "Cập nhật sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CarModel> call, Throwable t) {
                        // Xử lý lỗi nếu yêu cầu gặp sự cố
                        Log.e("Update", "Error updating sản phẩm: " + t.getMessage());
                        Toast.makeText(context, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.show();
    }


}
