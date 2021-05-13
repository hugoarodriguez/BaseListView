package com.example.baselistview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etProducto;
    TextInputLayout tilProducto;
    ListView lvProductos;
    ArrayList<String> array_list;
    ArrayAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tilProducto = (TextInputLayout)findViewById(R.id.tilProducto);
        etProducto = (EditText)findViewById(R.id.etProducto);
        lvProductos = (ListView)findViewById(R.id.lvProductos);

        MostrarLista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id){
            case R.id.op_borrarLista:
                builder.setTitle("Borrar Lista")
                        .setMessage("¿Estás seguro que deseas borrar la lista de forma definitiva?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                borrar();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                break;

            case R.id.op_acercaDe:
                builder.setTitle("Acerca De")
                        .setMessage("Hecho por Hugo Alexander Rodríguez Cruz")
                        .setNeutralButton("Ok", null)
                        .show();


                break;

            default:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void agregar(View v){
        Base obj = new Base(this, "Productos", null, 1);
        SQLiteDatabase objDB = obj.getWritableDatabase();

        String nuevoProducto = etProducto.getText().toString();

        //En la siguiente pasamos el valor "null" para el "Id" ya que este es autoincrementable
        String query = "INSERT INTO Compras(Id, Nombre) VALUES ("+null+", '"+ nuevoProducto +"')";

        if(nuevoProducto.isEmpty()){
            tilProducto.setError("Debe escribir un producto");
        } else {
            objDB.execSQL(query);
            Toast.makeText(this,"Producto agregado satisfactoriamente", Toast.LENGTH_SHORT).show();

            etProducto.setText("");
            tilProducto.setError(null);
            etProducto.requestFocus();
            MostrarLista();
        }

        objDB.close();
    }

    public void borrar(){
        Base obj = new Base(this, "Productos", null, 1);
        SQLiteDatabase objDB = obj.getWritableDatabase();

        Cursor cursor = objDB.rawQuery("SELECT * FROM Compras", null);

        if(cursor.moveToNext()){
            //Eliminamos todos los registros de nuestra tabla "Compras"
            objDB.delete("Compras", null, null);
            //Limpiamos nuestro ArrayList
            array_list.clear();

            //Notificamos que los datos se han modificado, cualquier vista que refleje el conjunto
            //de datos debe actualizarse
            adaptador.notifyDataSetChanged();

            //Reiniciamos el campo "Id" que es autoincrementable de la tabla "Compras"
            objDB.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'Compras'");

            Toast.makeText(this, "Se han eliminado todos los registros", Toast.LENGTH_SHORT).show();
            objDB.close();
        }
    }

    public void borrarProducto(String idProducto){
        Base obj = new Base(this, "Productos", null, 1);
        SQLiteDatabase objDB = obj.getWritableDatabase();

        Cursor cursor = objDB.rawQuery("SELECT * FROM Compras WHERE ID = ?", new String[]{idProducto});

        if(cursor.moveToNext()){
            //Eliminamos todos los registros de nuestra tabla "Compras"
            objDB.delete("Compras", "Id = ?", new String[]{idProducto});
            //Limpiamos nuestro ArrayList
            array_list.clear();

            //Notificamos que los datos se han modificado, cualquier vista que refleje el conjunto
            //de datos debe actualizarse
            adaptador.notifyDataSetChanged();

            Toast.makeText(this, "Se ha eliminado el producto seleccionado", Toast.LENGTH_SHORT).show();
            objDB.close();
        }

        MostrarLista();
    }

    public void MostrarLista(){
        Base obj = new Base(this, "Productos", null, 1);
        SQLiteDatabase objDB = obj.getWritableDatabase();

        array_list = new ArrayList<String>();

        Cursor cursor = objDB.rawQuery("SELECT * FROM Compras", null);
        cursor.moveToFirst();
        while(cursor.isAfterLast() == false){
            array_list.add(cursor.getString(cursor.getColumnIndex("Id")) + " - "
            + (cursor.getString(cursor.getColumnIndex("Nombre"))));
            cursor.moveToNext();
        }

        adaptador = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array_list);
        lvProductos.setAdapter(adaptador);
        clickListViewItem();
        objDB.close();
    }

    public void modificarProducto(String idProducto, String producto){
        //TODO: Agregar las funciones necesarias para modificar un producto
    }

    public void clickListViewItem(){
        lvProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getResources().getString(R.string.eliminar_title))
                        .setMessage(getResources().getString(R.string.eliminar_message))
                        .setPositiveButton(R.string.delete_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Obtenmos el id del producto seleccionado
                                String producto = lvProductos.getItemAtPosition(position).toString();
                                int guionPosition = producto.indexOf("-");
                                String idProducto = producto.substring(0, guionPosition-1).toString();

                                borrarProducto(idProducto);

                            }
                        })
                        .setNegativeButton(R.string.modify_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Obtenmos el id del producto seleccionado
                                String producto = lvProductos.getItemAtPosition(position).toString();
                                int guionPosition = producto.indexOf("-");
                                String idProducto = producto.substring(0, guionPosition-1).toString();

                                //TODO: Agregar modificarProductos()

                            }
                        })
                        .setNeutralButton(R.string.nothing_button, null)
                        .show();
            }
        });
    }
}