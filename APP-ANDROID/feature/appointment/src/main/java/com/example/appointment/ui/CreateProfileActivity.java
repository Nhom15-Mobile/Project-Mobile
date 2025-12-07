package com.example.appointment.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appointment.R;
import com.example.appointment.api.CareProfileService;
import com.example.appointment.api.LocationService;
import com.example.appointment.api.OCRService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.uithealthcare.domain.careProfile.CreateCareProfileRequest;
import com.uithealthcare.domain.careProfile.CreateCareProfileResponse;
import com.uithealthcare.domain.location.District;
import com.uithealthcare.domain.location.DistrictResponse;
import com.uithealthcare.domain.location.Province;
import com.uithealthcare.domain.location.ProvinceResponse;
import com.uithealthcare.domain.location.Ward;
import com.uithealthcare.domain.location.WardResponse;
import com.uithealthcare.domain.ocr.CccdData;
import com.uithealthcare.domain.ocr.OcrResponse;
import com.uithealthcare.network.ApiServices;
import com.uithealthcare.network.SessionInterceptor;
import com.uithealthcare.util.ConvertDate;
import com.uithealthcare.util.HandleAutoComplete;
import com.uithealthcare.util.ScanManager;
import com.uithealthcare.util.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateProfileActivity extends AppCompatActivity {
    private TextInputEditText etFullName, etPhone, etRelation, etDob, etAddressDetail;

    private MaterialAutoCompleteTextView autoCountry, autoGender, autoProvince, autoDistrict, autoWard;

    private MaterialButton btnCreate, btnBack, btnScan;
    private CareProfileService careProfileService;
    private LocationService locationService;
    private OCRService ocrService;
    private List<Province> provinceList;
    private List<District> districtList;
    private List<Ward> wardList;
    private final List<String> genderList = Arrays.asList("Nam", "Nữ", "Khác");
    private final List<String> countryList = List.of("Việt Nam");
    private String selectedProvinceCode;
    private String selectedDistrictCode;
    private String selectedWardCode;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile);
        SessionManager sessionManager = new SessionManager(this);
        SessionInterceptor.TokenProvider tokenProvider = new SessionInterceptor.TokenProvider() {
            @Override
            public String getToken() {
                return sessionManager.getBearer();
            }
        };

        careProfileService = ApiServices.create(CareProfileService.class, tokenProvider);
        locationService = ApiServices.create(LocationService.class, tokenProvider);
        ocrService = ApiServices.create(OCRService.class, tokenProvider);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        processOCR(imageUri, ocrService);
                    }
                }
        );

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                isSuccess -> {
                    if (isSuccess && ScanManager.cameraImageUri != null) {
                        // Dùng Uri từ ScanManager
//                        processOCR(ScanManager.cameraImageUri);
                        Toast.makeText(CreateProfileActivity.this, "Chụp ảnh thành công",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );

        initView();

        loadProvinces(locationService);
        initEvent();
        btnScan.setOnClickListener(v -> {
            ScanManager.openGallery(pickImageLauncher);
        });
    }

    private void initView(){
        etFullName = findViewById(R.id.edtFullName);
        etPhone = findViewById(R.id.edtPhone);
        etRelation = findViewById(R.id.edtRelation);
        autoCountry = findViewById(R.id.autoCountry);
        autoGender = findViewById(R.id.autoGender);
        etDob = findViewById(R.id.edtDob);
        autoProvince = findViewById(R.id.autoProvince);
        autoDistrict = findViewById(R.id.autoDistrict);
        autoWard = findViewById(R.id.autoWard);
        etAddressDetail = findViewById(R.id.edtAddress);

        HandleAutoComplete.setupDropDown(autoCountry, countryList);
        HandleAutoComplete.setupDropDown(autoGender, genderList);

        btnCreate = findViewById(R.id.btnCreate);
        btnBack = findViewById(R.id.btnBack);
        btnScan = findViewById(R.id.btnScan);

        loadingDialog = new LoadingDialog(this);
    }

    private void initEvent(){
        btnBack.setOnClickListener(v -> finish());
        btnCreate.setOnClickListener(v -> sendRequest(careProfileService));
    }

    private CreateCareProfileRequest createRequest(){
        // Lấy từ TextInputEditText
        String fullName = etFullName.getText() != null
                ? etFullName.getText().toString().trim()
                : "";

        String phone = etPhone.getText() != null
                ? etPhone.getText().toString().trim()
                : "";

        String relation = etRelation.getText() != null
                ? etRelation.getText().toString().trim()
                : "";

        String dob = etDob.getText() != null
                ? ConvertDate.VNtoDateUS(etDob.getText().toString().trim())
                : "";

        String addressDetail = etAddressDetail.getText() != null
                ? etAddressDetail.getText().toString().trim()
                : "";


        // Lấy từ MaterialAutoCompleteTextView
        String country = autoCountry.getText() != null
                ? autoCountry.getText().toString().trim()
                : "";

        String gender = autoGender.getText() != null
                ? autoGender.getText().toString().trim()
                : "";


        return new CreateCareProfileRequest(fullName, relation, phone, country, gender,
                dob, selectedProvinceCode, selectedDistrictCode, selectedWardCode, addressDetail);
    }

    private void sendRequest(CareProfileService careProfileService){
        CreateCareProfileRequest request = createRequest();
        careProfileService.createCareProfile(request).enqueue(new Callback<CreateCareProfileResponse>() {
            @Override
            public void onResponse(Call<CreateCareProfileResponse> call, Response<CreateCareProfileResponse> response) {
                if(response.isSuccessful() && response.body()!= null){
                    Toast.makeText(CreateProfileActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
//                if (!response.isSuccessful() && response.body() != null) {
//                    Toast.makeText(CreateProfileActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
//                }
            }

            @Override
            public void onFailure(Call<CreateCareProfileResponse> call, Throwable throwable) {
                Toast.makeText(CreateProfileActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void loadProvinces(LocationService locationService){
        locationService.getProvinces().enqueue(new Callback<ProvinceResponse>() {
            @Override
            public void onResponse(Call<ProvinceResponse> call, Response<ProvinceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    provinceList = response.body().getData();
                    HandleAutoComplete.setupDropDown(autoProvince, provinceList);
                    autoProvince.setOnItemClickListener((parent, view, position, id) -> {
                        autoDistrict.setText("");
                        autoWard.setText("");

                        Province selected = (Province) parent.getItemAtPosition(position);
                        selectedProvinceCode = selected.getCode();
                        loadDistricts(locationService, selectedProvinceCode);
                    });
                } else {
                    Toast.makeText(CreateProfileActivity.this,
                            "Không lấy được tỉnh: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ProvinceResponse> call, Throwable throwable) {
                Toast.makeText(CreateProfileActivity.this,
                        throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadDistricts(LocationService locationService, String provinceCode){
        locationService.getDistricts(provinceCode).enqueue(new Callback<DistrictResponse>() {
            @Override
            public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    districtList = response.body().getData();
                    HandleAutoComplete.setupDropDown(autoDistrict,districtList);
                    autoDistrict.setOnItemClickListener((parent, view, position, id) -> {
                        autoWard.setText("");
                        District selected = (District) parent.getItemAtPosition(position);
                        selectedDistrictCode = selected.getCode();
                        loadWard(locationService, selectedDistrictCode);
                    });
                }
                else {
                    Toast.makeText(CreateProfileActivity.this, "Không lấy được huyện: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DistrictResponse> call, Throwable throwable) {
                Toast.makeText(CreateProfileActivity.this,
                        throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadWard(LocationService locationService, String districtCode){
        locationService.getWards(districtCode).enqueue(new Callback<WardResponse>() {
            @Override
            public void onResponse(Call<WardResponse> call, Response<WardResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    wardList = response.body().getData();
                    HandleAutoComplete.setupDropDown(autoWard, wardList);
                    autoWard.setOnItemClickListener((parent, view, position, id) -> {
                        Ward selected = (Ward) parent.getItemAtPosition(position);
                        selectedWardCode = selected.getCode();
                    });
                }
                else {
                    Toast.makeText(CreateProfileActivity.this,  "Không lấy được phường: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<WardResponse> call, Throwable throwable) {
                Toast.makeText(CreateProfileActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void processOCR(Uri imageUri, OCRService ocrService) {
        loadingDialog.show();
        try {
            // 1. Đọc bytes từ Uri
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[4096];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] imageBytes = buffer.toByteArray();

            // 2. Tạo RequestBody + MultipartBody.Part
            okhttp3.RequestBody requestFile =
                    okhttp3.RequestBody.create(
                            imageBytes,
                            okhttp3.MediaType.parse("image/*")
                    );

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", "cccd.jpg", requestFile);

            // 3. Gọi API
            ocrService.uploadCccd(body).enqueue(new Callback<OcrResponse>() {
                @Override
                public void onResponse(Call<OcrResponse> call, Response<OcrResponse> response) {
                    loadingDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        OcrResponse ocr = response.body();
                        CccdData data = ocr.getData();

                        String msg = "Tên: " + data.getFullName()
                                + "\nQuốc gia: " + data.getCountry()
                                + "\nGiới tính: " + data.getGender()
                                + "\nNgày sinh: " + data.getDateOfBirth()
                                + "\nĐịa chỉ: " + data.getAddress();

                        etFullName.setText(data.getFullName());
                        autoGender.setText(data.getGender());
                        autoCountry.setText(data.getCountry());
                        etDob.setText(data.getDateOfBirth());
                        etAddressDetail.setText(data.getAddress());

                        Toast.makeText(CreateProfileActivity.this, "Scan hoàn tất", Toast.LENGTH_LONG).show();
                        Log.d("MyOCR", msg);
                    } else {
                        Toast.makeText(CreateProfileActivity.this,
                                "OCR thất bại: " + response.code(), Toast.LENGTH_LONG).show();
                        Log.d("MyOCR", "OCR thất bại: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<OcrResponse> call, Throwable throwable) {
                    loadingDialog.dismiss();
                    Toast.makeText(CreateProfileActivity.this,
                            "Lỗi mạng: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("MyOCR", "Lỗi mạng: " + throwable.getMessage());
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
            loadingDialog.dismiss();
            Toast.makeText(this, "Lỗi đọc ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
