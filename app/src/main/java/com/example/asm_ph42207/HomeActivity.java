package com.example.asm_ph42207;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {
    RecyclerView rcvHome;
    List<CarModel> listCarModel;

    FloatingActionButton add_sp;

    CarAdapter carAdapter;

    APIService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        rcvHome = findViewById(R.id.rcvDanhSach);
        add_sp = findViewById(R.id.floatAddDanhSach);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvHome.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvHome.addItemDecoration(dividerItemDecoration);

        add_sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opendialog();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api= retrofit.create(APIService.class);


        Call<List<CarModel>> call = api.getCars();
        api.deleteCar("ID_CUA_SAN_PHAM_CAN_XOA");
        call.enqueue(new Callback<List<CarModel>>() {
            @Override
            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                if (response.isSuccessful()) {
                    listCarModel = response.body();
                    carAdapter = new CarAdapter(getApplicationContext(),
                            listCarModel);
                    rcvHome.setAdapter(carAdapter);
                    carAdapter.setOnDeleteClickListener(new CarAdapter.OnDeleteClickListener() {
                        @Override
                        public void onDeleteClick(int pos) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this
                            );
                            builder.setIcon(R.drawable.add);
                            builder.setTitle("Thông báo");
                            builder.setMessage("Bạn có muốn xóa sản phẩm " + " không");
                            builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                    deleteSanPham(pos);
                                }
                            });
                            builder.setNegativeButton("không", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(HomeActivity.this, "Không xóa", Toast.LENGTH_SHORT).show();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                    });

                    carAdapter.setOnEditClickListener(new CarAdapter.OnEditClickListener() {
                        @Override
                        public void onEditClick(int pos) {
                            opendialogsua(listCarModel.get(pos));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<CarModel>> call, Throwable t) {

                Log.e("Main", t.getMessage());
            }
        });
    }

    public void opendialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.item_add_car, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        TextInputEditText edt_ten = view.findViewById(R.id.edt_add_ten_xe);
        TextInputEditText edt_namSX = view.findViewById(R.id.edt_add_namSX_xe);
        TextInputEditText edt_hang = view.findViewById(R.id.edt_add_hang_xe);
        TextInputEditText edt_gia = view.findViewById(R.id.edt_add_gia_xe);
        Button btnadd = view.findViewById(R.id.btn_ADD_SP);
        Button btnaddclose = view.findViewById(R.id.btn_HuyADD_SP);

        btnaddclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenSP = edt_ten.getText().toString();
                int namSX = Integer.parseInt(edt_namSX.getText().toString());
                String hang = edt_hang.getText().toString();
                double gia = Double.parseDouble(edt_gia.getText().toString());

                Log.e("loi", tenSP + namSX + hang + gia);


                // Tạo một đối tượng sanPhammodel mới
                CarModel newCar = new CarModel(tenSP,namSX,hang, gia);
                // Gửi yêu cầu POST để thêm sản phẩm mới
                newCar.setTen(tenSP);
                newCar.setNamSX(namSX);
                newCar.setHang(hang);
                newCar.setGia(gia);

                Call<CarModel> call = api.addCar(newCar);
                call.enqueue(new Callback<CarModel>() {
                    @Override
                    public void onResponse(Call<CarModel> call, Response<CarModel> response) {
                        if (response.isSuccessful()) {
                            // Thêm sản phẩm mới thành công
                            CarModel addedSanPham = response.body();
                            listCarModel.add(addedSanPham);
                            carAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                            Toast.makeText(HomeActivity.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss(); // Đóng dialog sau khi thêm thành công
                        } else {
                            // Xử lý lỗi nếu yêu cầu không thành công
                            Toast.makeText(HomeActivity.this, "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CarModel> call, Throwable t) {
                        // Xử lý lỗi nếu yêu cầu gặp sự cố
                        Log.e("AddActivity", "Error adding sản phẩm: " + t.getMessage());
                        Toast.makeText(HomeActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.show();
    }

    public void updatesanpham(int pos) {
        String id = listCarModel.get(pos).get_id();
        Call<Void> call = api.deleteCar(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Xóa sản phẩm thành công, cập nhật lại danh sách sản phẩm và refresh RecyclerView
                    listCarModel.remove(pos);
                    carAdapter.notifyItemRemoved(pos);
                    Toast.makeText(HomeActivity.this, "xoa thanh cong", Toast.LENGTH_SHORT).show();
                } else {
                    // Xóa sản phẩm thất bại
                    Toast.makeText(HomeActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Delete", t.getMessage());
                Toast.makeText(HomeActivity.this, "Lỗi khi xóa sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void opendialogsua(CarModel carModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.item_update_car, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        TextInputEditText edtten = view.findViewById(R.id.edt_ten_xe);
        TextInputEditText edtnamSX = view.findViewById(R.id.edt_namSX_xe);
        TextInputEditText edthang = view.findViewById(R.id.edt_hang_xe);
        TextInputEditText edtgia = view.findViewById(R.id.edt_gia_xe);
        Button btnUpdate = view.findViewById(R.id.btn_Update_SP);
        Button btnUpdateClose = view.findViewById(R.id.btn_Huy_Update_SP);
        edtten.setText(carModel.getTen());
        edtnamSX.setText(String.valueOf(carModel.getNamSX()));
        edthang.setText(String.valueOf(carModel.getHang()));
        edtgia.setText(String.valueOf(carModel.getGia()));

        btnUpdateClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnUpdate.setText("Cập nhật");
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenSP = edtten.getText().toString();
                int namSX = Integer.parseInt(edtnamSX.getText().toString());
                String hang = edthang.getText().toString();
                double gia = Double.parseDouble(edtgia.getText().toString());


                Log.e("loi", tenSP + namSX + hang + gia);
                carModel.setTen(tenSP);
                carModel.setNamSX(namSX);
                carModel.setHang(hang);
                carModel.setGia(gia);
                // Tạo một đối tượng sanPhammodel mới
                Call<CarModel> call = api.updateCar(carModel.get_id(), carModel);
                call.enqueue(new Callback<CarModel>() {
                    @Override
                    public void onResponse(Call<CarModel> call, Response<CarModel> response) {
                        if (response.isSuccessful()) {
                            // Cập nhật sản phẩm thành công
                            carAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                            Toast.makeText(HomeActivity.this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss(); // Đóng dialog sau khi cập nhật thành công
                        } else {
                            // Xử lý lỗi nếu yêu cầu không thành công
                            Toast.makeText(HomeActivity.this, "Cập nhật sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CarModel> call, Throwable t) {
                        // Xử lý lỗi nếu yêu cầu gặp sự cố
                        Log.e("Update", "Error updating sản phẩm: " + t.getMessage());
                        Toast.makeText(HomeActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.show();
    }

}