package com.bekirgy.yemektarifiapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_tarif.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.jar.Manifest
import kotlin.math.max
import android.net.Uri as UriI


class fragment_tarif : Fragment() {
    var secilenGorsel  : UriI? = null
    var secilenBitmap : Bitmap? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tarif, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button.setOnClickListener {
            kaydet(it)
        }

        imageView.setOnClickListener {
            gorselEkle(it)
        }

        arguments?.let {
            val gelenbilgi=fragment_tarifArgs.fromBundle(it).bilgi
            if (gelenbilgi.equals("menudengeldim")){
                //yeni bir yemek eklemeye geldi
                YemekisimText.setText("")
                MalzemelerText.setText("")

                button.visibility=View.VISIBLE

                val gorselsecimarkaPlani=BitmapFactory.decodeResource(context?.resources,R.drawable.bdsiz)
                imageView.setImageBitmap(gorselsecimarkaPlani)


            }else {
                //kaydedileni görüntülemeye geldi

                button.visibility=View.INVISIBLE

                val gelenid=fragment_tarifArgs.fromBundle(it).id


                context?.let {
                    try {
                        val db=it.openOrCreateDatabase("YemekApp",Context.MODE_PRIVATE,null)

                        val cursor=db.rawQuery("SELECT * FROM yemekler WHERE id=?", arrayOf(gelenid.toString()))

                        val yemekismiIndex=cursor.getColumnIndex("yemekadi")
                        val yemekmalzemeIndex=cursor.getColumnIndex("yemektarifi")
                        val gorselIndex=cursor.getColumnIndex("resim")

                        while (cursor.moveToNext()){

                            YemekisimText.setText(cursor.getString(yemekismiIndex))
                            MalzemelerText.setText(cursor.getString(yemekmalzemeIndex))

                            val bytedizisi=cursor.getBlob(gorselIndex)
                            val bitmap=BitmapFactory.decodeByteArray(bytedizisi,0, bytedizisi.size)
                            imageView.setImageBitmap(bitmap)

                        }
                        cursor.close()

                    }catch (e:Exception){

                        e.printStackTrace()
                    }

                }
            }

        }


    }

    fun kaydet(view: View){
        val yemekadi= YemekisimText.text.toString()
        val yemekmalzeme=MalzemelerText.text.toString()

        if (secilenBitmap !=null){
            val kucukBitmap= BitmapKucult(secilenBitmap!!,300)

            val outputStream=ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.JPEG,50,outputStream)
            val byteDizisi=outputStream.toByteArray()

            try {
                context?.let {
                    val database=it.openOrCreateDatabase("YemekApp", Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS yemekler(id INTEGER PRIMARY KEY, yemekadi VARCHAR, yemektarifi VARCHAR, resim BLOB)")
                    // database.execSQL("INSERT INTO yemekler(yemekadi,yemektarifi,resim) VALUES (?,?,?)")
                    val sqlString="INSERT INTO yemekler(yemekadi,yemektarifi,resim) VALUES (?,?,?)"
                    val statement= database.compileStatement(sqlString)

                    statement.bindString(1,yemekadi)
                    statement.bindString(2,yemekmalzeme)
                    statement.bindBlob(3,byteDizisi)
                    statement.execute()
                }




            }catch (e: Exception){
                e.printStackTrace()
            }

            val action=fragment_tarifDirections.actionFragmentTarifToFragmentListe()
            Navigation.findNavController(view).navigate(action)
        }


    }
    fun gorselEkle(view: View){
        val let = activity?.let {

            if (ContextCompat.checkSelfPermission(
                    it.applicationContext,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //izin verilmedi izin istememiz gerekiyor
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)

            } else {

                //izin verildi bir daha izin istenmesi gerekli değil
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)

            }

        }




    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode==1){
            if (grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode==2 && resultCode==Activity.RESULT_OK && data != null){

            secilenGorsel=data.data


            try {
                context?.let {
                    if(secilenGorsel!=null) {
                        if (Build.VERSION.SDK_INT>=28){
                            val source = ImageDecoder.createSource(it.contentResolver, secilenGorsel!!)
                            secilenBitmap=ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(secilenBitmap)

                        }else{
                            secilenBitmap=MediaStore.Images.Media.getBitmap(it.contentResolver,secilenGorsel)
                            imageView.setImageBitmap(secilenBitmap)

                        }


                    }
                }




            }catch (e:Exception){
                e.printStackTrace()

            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun BitmapKucult(kullaniciniSectigiBitmap : Bitmap,maximumBoyut : Int) : Bitmap{

        var width= kullaniciniSectigiBitmap.width
        var height= kullaniciniSectigiBitmap.height

        var BitmapOrani: Double= width.toDouble()/height.toDouble() //yatay mı dikey mi

        if (BitmapOrani>1){
            //yataysa görsel
            width=maximumBoyut
            val kisaltilmisHeight= width/BitmapOrani
            height=kisaltilmisHeight.toInt()

        }else{
            //dikeyse görsel
            height= maximumBoyut
            val kisaltilmisWidth=height*BitmapOrani
            width=kisaltilmisWidth.toInt()
        }

        return Bitmap.createScaledBitmap(kullaniciniSectigiBitmap,width,height,true)
    }



}