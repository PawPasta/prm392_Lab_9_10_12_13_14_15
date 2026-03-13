package com.prm392_sp26.se182138_lab9;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Database database;
    ListView lvCongViec;
    ArrayList<CongViec> arrayCongViec;
    CongViecAdapter adapter;
    FloatingActionButton fabAddCongViec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvCongViec = findViewById(R.id.listviewCongViec);
        fabAddCongViec = findViewById(R.id.fabAddCongViec);
        arrayCongViec = new ArrayList<>();
        adapter = new CongViecAdapter(this, R.layout.dong_cong_viec, arrayCongViec);
        lvCongViec.setAdapter(adapter);

        // Tao database GhiChu
        database = new Database(this, "Ghichu.sqlite", null, 1);

        // Tao table CongViec
        database.QueryData("Create table if not exists CongViec(" +
                "Id Integer Primary Key Autoincrement, " +
                "TenCV nvarchar(200))");

        // Insert sample data if empty
        Cursor checkData = database.GetData("Select Id from CongViec");
        if (checkData.getCount() == 0) {
            database.QueryData("Insert into CongViec values(null, 'Project Android')");
            database.QueryData("Insert into CongViec values(null, 'Design app')");
        }
        checkData.close();

        GetDataCongViec();

        fabAddCongViec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogThem();
            }
        });
    }

    private void GetDataCongViec() {
        arrayCongViec.clear();
        Cursor dataCongViec = database.GetData("Select * from CongViec");
        while (dataCongViec.moveToNext()) {
            int id = dataCongViec.getInt(0);
            String ten = dataCongViec.getString(1);
            arrayCongViec.add(new CongViec(id, ten));
        }
        dataCongViec.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_congviec, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuAddCongViec) {
            DialogThem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DialogThem() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_them_cong_viec);

        EditText editTen = dialog.findViewById(R.id.editTextTenCV);
        Button btnThem = dialog.findViewById(R.id.buttonThem);
        Button btnHuy = dialog.findViewById(R.id.buttonHuy);

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tencv = editTen.getText().toString();
                if (tencv.equals("")) {
                    Toast.makeText(MainActivity.this,
                            "Vui long nhap ten cong viec !",
                            Toast.LENGTH_SHORT).show();
                } else {
                    database.QueryData("Insert into CongViec values(null, '" + tencv + "')");
                    Toast.makeText(MainActivity.this, "Da them", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    GetDataCongViec();
                }
            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void DialogSuaCongViec(String ten, int id) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_sua);

        EditText editTen = dialog.findViewById(R.id.editTextTenCV);
        Button btnXacNhan = dialog.findViewById(R.id.buttonXacNhan);
        Button btnHuy = dialog.findViewById(R.id.buttonHuyEdit);

        editTen.setText(ten);

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenMoi = editTen.getText().toString().trim();
                if (tenMoi.equals("")) {
                    Toast.makeText(MainActivity.this,
                            "Vui long nhap ten cong viec !",
                            Toast.LENGTH_SHORT).show();
                } else {
                    database.QueryData("Update CongViec set TenCV = '" + tenMoi + "' where Id = '" + id + "'");
                    Toast.makeText(MainActivity.this, "Da cap nhat", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    GetDataCongViec();
                }
            }
        });

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void DialogXoaCongViec(String ten, int id) {
        AlertDialog.Builder dialogXoa = new AlertDialog.Builder(this);
        dialogXoa.setMessage("Ban co muon xoa cong viec '" + ten + "' khong?");
        dialogXoa.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                database.QueryData("Delete from CongViec where Id = '" + id + "'");
                Toast.makeText(MainActivity.this, "Da xoa " + ten, Toast.LENGTH_SHORT).show();
                GetDataCongViec();
            }
        });

        dialogXoa.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        dialogXoa.show();
    }
}
