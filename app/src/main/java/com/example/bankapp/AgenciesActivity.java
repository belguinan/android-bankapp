package com.example.bankapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgenciesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;
    private DBHelper dbHelper;
    private List<Agency> agencyList;
    private Map<Marker, Agency> markerAgencyMap;
    private SearchView searchView;

    private CardView infoCard;
    private TextView agencyNameText, agencyAddressText, agencyPhoneText;
    private Button callButton, smsButton, emailButton;

    private Agency selectedAgency;
    private static final int REQUEST_CALL_PERMISSION = 1;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agencies);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.infoCard = findViewById(R.id.info_card);
        this.agencyNameText = findViewById(R.id.agency_name);
        this.agencyAddressText = findViewById(R.id.agency_address);
        this.agencyPhoneText = findViewById(R.id.agency_phone);
        this.callButton = findViewById(R.id.btn_call);
        this.smsButton = findViewById(R.id.btn_sms);
        this.emailButton = findViewById(R.id.btn_email);
        this.searchView = findViewById(R.id.search_view);

        this.infoCard.setVisibility(View.GONE);
        this.callButton.setVisibility(View.GONE);
        this.smsButton.setVisibility(View.GONE);
        this.emailButton.setVisibility(View.GONE);

        this.mapView = findViewById(R.id.map_view);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        this.mapView.onCreate(mapViewBundle);
        this.mapView.getMapAsync(this);

        this.dbHelper = new DBHelper(this);
        this.markerAgencyMap = new HashMap<>();

        this.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        this.smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });

        this.emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateMapWithFilteredAgencies(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateMapWithFilteredAgencies(newText);
                return true;
            }
        });

        this.initializeAgencies();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        this.mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.mapView.onStop();
    }

    @Override
    protected void onPause() {
        this.mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        this.mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        this.mapView.onLowMemory();
    }

    private void initializeAgencies() {

        if (this.dbHelper.getAgenciesCount() == 0) {
            this.dbHelper.addAgency(new Agency("التجاري وفا بنك", "P5V8+VWC, Av. Arabie Saoudite, Tanger", "0539384247", "support@attijarbank.ma", 35.744682, -5.8422673));
            this.dbHelper.addAgency(new Agency("Attijariwafa Bank", "P5Q9+R9F, Tangier", "0629015949", "support@attijarbank.ma", 35.744682, -5.8422673));
            this.dbHelper.addAgency(new Agency("Attijariwafa Bank", "Q53C+9QM, Tanger 90060", "0629015949", "support@attijarbank.ma", 35.763855, -5.8392815));
            this.dbHelper.addAgency(new Agency("Attijariwafa Bank Bendibane", "Q54H+WCG, Rue Martyr Abdelhadi Taib, Tanger 90060", "0539319600", "support@attijarbank.ma", 35.763855, -5.8392815));
            this.dbHelper.addAgency(new Agency("Attijariwafa Bank", "20, Avenue Chahid Med Benseddik, Lot Florencia. Quartier Bendibane, Tangier", "0539319600", "support@attijarbank.ma", 35.7538401, -5.8268559));
            this.dbHelper.addAgency(new Agency("Attijariwafa Bank", "P5MV+VX6, Tanger 90060", "0601136490", "support@attijarbank.ma", 35.7346739, -5.8146375));
            this.dbHelper.addAgency(new Agency("Attijariwafa Bank", "77 Av. Anfa, Tanger 90060", "0539339511", "support@attijarbank.ma", 35.7700117, -5.8355551));
            this.dbHelper.addAgency(new Agency("Attijariwafa Bank", "Q57J+PVW RES MY ABDZLAZIZ, Av. Moulay Abdelaziz, Tangier 90060", "0539322401", "attijari.bank@gmail.com", 35.7643719, -5.8273102));
            this.dbHelper.addAgency(new Agency("Attijariwafa Bank", "Residence Sanae Plage, Ave Mohammed VI, Tangier", "0539329495", "attijari.bank@gmail.com", 35.7747827, -5.7926081));
            this.dbHelper.addAgency(new Agency("CIH Bank", "Bd Ibntachfine, Rue Jamal Eddine Al Afghani, Tanger 90060", "0539341127", "cih.bank@gmail.com", 35.7749769, -5.840223));
            this.dbHelper.addAgency(new Agency("CIH Bank", "Av. Arabie Saoudite Av Med Vi,quar.ahlen Bloc A1, Resi.habiba, Tangier 90060", "0539319110", "cih.bank@gmail.com", 35.7476081, -5.8788637));
            this.dbHelper.addAgency(new Agency("CIH Bank", "P5XR+PFF, Av. Aicha Moussafer, Tanger 90060", "0539319110", "cih.bank@gmail.com", 35.7493068, -5.8469217));
            this.dbHelper.addAgency(new Agency("Bank of Africa", "Q576+284, Av. Moulay Rachid, Tanger", "", "", 35.7625049, -5.8772737));
            this.dbHelper.addAgency(new Agency("Bank of Africa", "Q682+3W3, Tangier 90000", "0539328280", "", 35.7651477, -5.8357983));
            this.dbHelper.addAgency(new Agency("Banque Populaire", "P5Q9+VCR, Tangier", "", "", 35.7397254, -5.8696038));
            this.dbHelper.addAgency(new Agency("Banque Populaire", "47 Ave Mohammed VI, Tangier", "", "", 35.7751196, -5.824904));
            this.dbHelper.addAgency(new Agency("Banque Populaire", "169, lotissement Azizia résidence Saad immeuble 6, Tanger", "0539385934", "", 35.7341639, -5.8890611));
            this.dbHelper.addAgency(new Agency("Banque Populaire", "Tangier 90060", "", "", 35.751208, -5.8476698));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        this.agencyList = this.dbHelper.getAllAgencies();
        this.updateMapWithFilteredAgencies("");

        this.mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                selectedAgency = markerAgencyMap.get(marker);
                if (selectedAgency != null) {
                    displayAgencyInfo(selectedAgency);
                }
                return false;
            }
        });
    }

    private void updateMapWithFilteredAgencies(String query) {
        this.mMap.clear();
        this.markerAgencyMap.clear();

        List<Agency> filteredAgencies;

        if (query != null && !query.isEmpty()) {
            filteredAgencies = new ArrayList<>();
            for (Agency agency : this.agencyList) {
                if (agency.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredAgencies.add(agency);
                }
            }
        } else {
            filteredAgencies = this.agencyList;
        }

        for (Agency agency : filteredAgencies) {
            LatLng position = new LatLng(agency.getLatitude(), agency.getLongitude());
            Marker marker = this.mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(agency.getName()));

            if (marker != null) {
                this.markerAgencyMap.put(marker, agency);
            }
        }

        if (!filteredAgencies.isEmpty()) {
            Agency firstAgency = filteredAgencies.get(0);
            LatLng position = new LatLng(firstAgency.getLatitude(), firstAgency.getLongitude());
            this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12));
        }
    }

    private void displayAgencyInfo(Agency agency) {
        this.agencyNameText.setText(agency.getName());
        this.agencyAddressText.setText(agency.getAddress());
        this.agencyPhoneText.setText(agency.getPhone());
        this.infoCard.setVisibility(View.VISIBLE);

        if (agency.getPhone() != null && !agency.getPhone().isEmpty()) {
            this.callButton.setVisibility(View.VISIBLE);
            this.smsButton.setVisibility(View.VISIBLE);
        } else {
            this.callButton.setVisibility(View.GONE);
            this.smsButton.setVisibility(View.GONE);
        }

        if (agency.getEmail() != null && !agency.getEmail().isEmpty()) {
            this.emailButton.setVisibility(View.VISIBLE);
        } else {
            this.emailButton.setVisibility(View.GONE);
        }
    }

    private void makePhoneCall() {
        if (this.selectedAgency != null) {
            String phone = this.selectedAgency.getPhone();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "Please select an agency first", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSMS() {
        if (this.selectedAgency != null) {
            String phone = this.selectedAgency.getPhone();
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + phone));
            intent.putExtra("sms_body", "Hello, I am a customer of your bank and i would like to ");
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please select an agency first", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail() {
        if (this.selectedAgency != null) {
            String email = this.selectedAgency.getEmail();
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Customer Inquiry");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello,\n\nI am a customer of your bank and I have a question about my account.\n");

            try {
                startActivity(Intent.createChooser(emailIntent, "Send email using..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please select an agency first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.makePhoneCall();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}