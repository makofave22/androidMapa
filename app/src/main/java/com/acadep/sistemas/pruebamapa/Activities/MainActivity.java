package com.acadep.sistemas.pruebamapa.Activities;


import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.acadep.sistemas.pruebamapa.Fragment.MapsFragment;
import com.acadep.sistemas.pruebamapa.Fragment.WelcomeFragment;
import com.acadep.sistemas.pruebamapa.R;
import com.google.android.gms.maps.MapFragment;

public class MainActivity extends AppCompatActivity {

    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toast.makeText(this,"Called",Toast.LENGTH_SHORT).show();

        //En caso de rotar el dispositivo
        if(savedInstanceState==null){
            currentFragment = new WelcomeFragment();
            changeFragment(currentFragment);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_welcome:
                currentFragment = new WelcomeFragment();
                break;
            case R.id.menu_map:
                currentFragment = new MapsFragment();
                break;
        }
        changeFragment(currentFragment);
        return super.onOptionsItemSelected(item);
    }

    private void changeFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,fragment).commit();
    }
}
