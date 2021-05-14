package com.example.baselistview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etProducto;
    TextInputLayout tilProducto;
    Button btnAgregar;
    ListView lvProductos;
    ArrayList<String> array_list;
    ArrayAdapter adaptador;

    private Menu myMenu;
    private int menuOptionAction = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tilProducto = (TextInputLayout)findViewById(R.id.tilProducto);
        etProducto = (EditText)findViewById(R.id.etProducto);
        btnAgregar = (Button)findViewById(R.id.btnAgregar);
        lvProductos = (ListView)findViewById(R.id.lvProductos);

        MostrarLista();
        changeButtonOnClickListener(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        myMenu = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id){
            case R.id.op_borrarLista:

                if(menuOptionAction == 1){
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
                } else if(menuOptionAction == 2){
                    builder.setTitle("Cancelar Modificación")
                            .setMessage("¿Estás seguro que deseas cancelar la modificación de este producto?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    changeButtonOnClickListener(null);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }

                break;

            case R.id.op_acercaDe:
                builder.setTitle("Acerca De")
                        .setMessage("Estudiante: Hugo Alexander Rodríguez Cruz\n\nCarnet: 25-0663-2017")
                        .setNeutralButton("Ok", null)
                        .show();

                break;

            default:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //Método para agregar un producto
    public void agregar(){
        Base obj = new Base(this, "Productos", null, 1);
        SQLiteDatabase objDB = obj.getWritableDatabase();

        String nuevoProducto = etProducto.getText().toString().trim();

        if(nuevoProducto.isEmpty()){
            tilProducto.setError("Debe escribir un producto");
        } else {

            //En la siguiente pasamos el valor "null" para el "Id" ya que este es autoincrementable
            String query = "INSERT INTO Compras(Id, Nombre) VALUES ("+null+", '"+ nuevoProducto +"')";

            objDB.execSQL(query);
            Toast.makeText(this,"Producto agregado satisfactoriamente", Toast.LENGTH_SHORT).show();

            etProducto.setText("");
            tilProducto.setError(null);
            etProducto.requestFocus();
            MostrarLista();
        }

        objDB.close();
    }

    //Método para borrar toda la lista de productos
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

    //Método para borrar un producto en específico de la lista
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


            Toast.makeText(this, "Se eliminó el producto seleccionado", Toast.LENGTH_SHORT).show();
            objDB.close();
        }

        MostrarLista();
    }

    //Método para mostrar la lista de productos
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

    //Método para modificar un producto
    public void modificarProducto(String idProducto){
        Base obj = new Base(this, "Productos", null, 1);
        SQLiteDatabase objDB = obj.getWritableDatabase();

        String producto = etProducto.getText().toString().trim();

        if(!producto.isEmpty()){

            Cursor cursor = objDB.rawQuery("SELECT * FROM Compras WHERE Id = ?", new String[]{idProducto});
            if(cursor.moveToFirst()){
                ContentValues contentValues = new ContentValues();
                contentValues.put("Nombre", producto);

                objDB.update("Compras", contentValues, "Id = ?", new String[]{idProducto});

                etProducto.setText("");
                tilProducto.setError(null);
                etProducto.requestFocus();

                Toast.makeText(this, "Se actualizó el producto seleccionado", Toast.LENGTH_SHORT).show();
            }
            MostrarLista();
            lvProductos.setVisibility(View.VISIBLE);//Ocultamos el ListView
            changeButtonOnClickListener(null);//Cambiamos el botón de "Modificar" a "Agregar"
        } else {
            tilProducto.setError("Ingresa el nuevo nombre del producto");
            etProducto.setText("");
            etProducto.requestFocus();
        }

        objDB.close();
    }

    //Método que asigna el ItemClickListener al ListView "lvProductos"
    public void clickListViewItem(){
        if(menuOptionAction == 1){
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
                                    String nomProducto = producto.substring(guionPosition+1, producto.length()).toString();

                                    etProducto.setText(nomProducto);
                                    changeButtonOnClickListener(idProducto);

                                }
                            })
                            .setNeutralButton(R.string.nothing_button, null)
                            .show();

                }
            });
        } else {
            lvProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        }

    }

    //Método para cambiar el OnClikListener del "btnAgregar" y su respectivo texto
    public void changeButtonOnClickListener(String idProducto){
        if(idProducto != null){
            //Acciones a ejecutar para Modificar

            if(myMenu != null){
                //Cambiamos el ícono del menú
                myMenu.getItem(0).setIcon(R.drawable.ic_baseline_cancel_24);
                //Determinamos la acción a ejecutar
                menuOptionAction = 2;

                //Cambiamos el listener del ListView
                clickListViewItem();
            }

            tilProducto.setError(null);
            tilProducto.setHint(R.string.hint_agg_producto2);
            btnAgregar.setText("Modificar");
            btnAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modificarProducto(idProducto);
                }
            });

        } else {
            //Acciones a ejecutar para Agregar

            if(myMenu != null){
                //Cambiamos el ícono del menú
                myMenu.getItem(0).setIcon(R.drawable.ic_baseline_delete_forever_24);
                //Determinamos la acción a ejecutar
                menuOptionAction = 1;

                //Cambiamos el listener del ListView
                clickListViewItem();
            }

            etProducto.setText(null);
            tilProducto.setError(null);
            tilProducto.setHint(R.string.hint_agg_producto);
            btnAgregar.setText("Agregar");
            btnAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agregar();
                }
            });
        }
    }
}