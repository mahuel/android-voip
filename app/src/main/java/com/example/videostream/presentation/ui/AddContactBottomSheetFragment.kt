package com.example.videostream.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.videostream.R
import com.example.videostream.presentation.viewmodel.AddContactViewModel
import com.example.videostream.utils.makeToast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode

@AndroidEntryPoint
class AddContactBottomSheetFragment : BottomSheetDialogFragment() {

    private val addContactViewModel: AddContactViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_fragment_add_contact, container)
        view.findViewById<ImageButton>(R.id.closeBtn).setOnClickListener {
            dismiss()
        }

        view.findViewById<ImageView>(R.id.qrCodeImageView).apply {
            setImageBitmap(addContactViewModel.getQRCodeBitmap())
        }

        view.findViewById<Button>(R.id.scanQrCode).setOnClickListener {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) -> {
                    startQRScanner()
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }


        return view
    }

    private fun startQRScanner() {
        scanQrCodeLauncher.launch(null)
    }

    private val scanQrCodeLauncher = registerForActivityResult(ScanQRCode()) { result ->
        when(result) {
            is QRResult.QRError -> somethingWentWrong()
            is QRResult.QRMissingPermission -> somethingWentWrong()
            is QRResult.QRUserCanceled -> somethingWentWrong()
            is QRResult.QRSuccess -> {
                if (!addContactViewModel.addAddress(
                        address = result.content.rawValue
                )) {
                    somethingWentWrong()
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startQRScanner()
            } else {
                context?.makeToast(
                    "You cannot scan a contact's QR code without the camera permission"
                )
            }
        }

    private fun somethingWentWrong() {
        context?.makeToast(
            "Something went wrong, please try again"
        )
    }

}