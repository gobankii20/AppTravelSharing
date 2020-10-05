package com.simple.travelsharing.view.user

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.simple.travelsharing.LoginActivity
import com.simple.travelsharing.R
import com.simple.travelsharing.data.Constants
import com.simple.travelsharing.data.SharedPreference
import com.simple.travelsharing.model.ModelCreateUser
import com.simple.travelsharing.utils.CustomDialogLoading
import com.simple.travelsharing.utils.Utils
import com.simple.travelsharing.utils.updateUser
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.custom_toolbar_main.view.*
import kotlinx.android.synthetic.main.fragment_profile_driver.*
import kotlinx.android.synthetic.main.fragment_profile_driver.etAddress
import kotlinx.android.synthetic.main.fragment_profile_driver.etFullName
import kotlinx.android.synthetic.main.fragment_profile_driver.etPhone
import kotlinx.android.synthetic.main.fragment_profile_driver.ivCard
import kotlinx.android.synthetic.main.fragment_profile_driver.ivProfile
import kotlinx.android.synthetic.main.fragment_profile_driver.view.*
import kotlinx.android.synthetic.main.fragment_profile_driver.view.etAddress
import kotlinx.android.synthetic.main.fragment_profile_driver.view.etFullName
import kotlinx.android.synthetic.main.fragment_profile_driver.view.etPhone
import kotlinx.android.synthetic.main.fragment_profile_driver.view.ivProfile
import kotlinx.android.synthetic.main.fragment_profile_user.*
import kotlinx.android.synthetic.main.fragment_profile_user.etEmail
import java.io.File
import java.util.HashMap


class ProfileUserFragment : Fragment() {

    val mRootRef = FirebaseDatabase.getInstance().reference.child("users")

    private var actionPhotos = "takePhotos"
    private var actionPhotosCard = "takePhotosCard"

    private var uriImageView: Uri? = null
    private var mActionPageCurrent = 0

    private val TAKE_PHOTO_REQUEST = 101
    private var mCurrentPhotoPath: String = ""

    private val TAKE_PHOTO_CARD_REQUEST = 102
    private var mCurrentPhotoPathCard: String = ""
    private var uriImageViewCard: Uri? = null

    private lateinit var mModelCreateUser: Map<String, Any?>

    var listCreateUser = ArrayList<ModelCreateUser>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_user, container, false)
        // Inflate the layout for this fragment
        initView(view)
        return view.rootView
    }

    private fun initView(view: View) {
        CustomDialogLoading.newProgressDialog(requireActivity())

        view.icToolbar.tvTitle.text = resources.getString(R.string.title_profile)
        initOnClick(view)

        onConnectListUser(Constants.USER_TYPE_USER)
    }

    private fun onConnectListUser(userRule: String) {
        CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))
        mRootRef.orderByChild("user_code")
            .equalTo(SharedPreference(requireContext()).getUserCode())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val list =
                            dataSnapshot.children.map { it.getValue(ModelCreateUser::class.java)!! }
                        if (list[0].user_rule.equals(userRule)) {
                            fetchData(list)
                        } else {
                            Toast.makeText(requireContext(), "ไม่พบผู้ใช้งาน", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "ไม่พบผู้ใช้งาน", Toast.LENGTH_SHORT)
                            .show()
                    }
                    CustomDialogLoading.dissProgressDialog()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    CustomDialogLoading.dissProgressDialog()
                    Utils.dialogMessage(
                        requireContext(),
                        "error : ${databaseError.message}"
                    )
                }
            })
    }

    private fun fetchData(list: List<ModelCreateUser>) {
        listCreateUser.clear()

        etFullName.setText(list[0].user_fullname)
        etPhone.setText(list[0].user_phone)
        etAddress.setText(list[0].user_address)
        etEmail.setText(list[0].user_email)

        if (list[0].user_image!!.isNotEmpty()) {
            Utils.setImageView(
                requireContext(),
                Utils.convertBase64ToImage(list[0].user_image!!),
                ivProfile
            )
        }

        listCreateUser.addAll(list)
    }

    private fun initOnClick(view: View) {
        view.icToolbar.layBackInvisible.setOnClickListener {
            Utils.dialogConfirmExit(requireActivity()) {
                SharedPreference(requireContext()).clear()
                val intentLogin = Intent(requireContext(), LoginActivity::class.java)
                intentLogin.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intentLogin)
            }
        }

        view.btnConfirm.setOnClickListener {
            updateProfile()
        }

        view.layoutImageProfile.setOnClickListener {
            validatePermissions()
        }
    }

    private fun updateProfile() {
        val mFullName = etFullName.text.toString()
        val mPhone = etPhone.text.toString()
        val mAddress = etAddress.text.toString()
        val mEmail = etEmail.text.toString()

        mModelCreateUser = ModelCreateUser(
            listCreateUser[0].user_code,
            if (mFullName.isNotEmpty()) mFullName else listCreateUser[0].user_fullname,
            if (mPhone.isNotEmpty()) mPhone else listCreateUser[0].user_phone,
            if (mEmail.isNotEmpty()) mEmail else listCreateUser[0].user_address,
            if (mAddress.isNotEmpty()) mAddress else listCreateUser[0].user_address,
            if (uriImageView != null) uriImageView?.let { it1 -> Utils.encodeImageToBase64(requireContext(), it1) } else listCreateUser[0].user_image,
            listCreateUser[0].user_name,
            listCreateUser[0].user_password,
            listCreateUser[0].user_rule,
            listCreateUser[0].date_request,
            listCreateUser[0].status,
            if (uriImageViewCard != null) uriImageViewCard?.let { it1 -> Utils.encodeImageToBase64(requireContext(), it1) } else listCreateUser[0].image_card
        ).updateUser().toMap()

        CustomDialogLoading.showProgressDialog(resources.getString(R.string.loading))
        val query = mRootRef.orderByChild("user_code").equalTo(SharedPreference(requireActivity()).getUserCode())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) { // dataSnapshot is the "issue" node with all children with id 0
                    for (dataSnapshot1 in dataSnapshot.children) {
                        val postValues = mModelCreateUser.toMap()
                        val childUpdates = HashMap<String, Any>()
                        childUpdates["/${dataSnapshot1.key}"] = postValues
                        mRootRef.updateChildren(childUpdates)
                    }
                }

                Handler().postDelayed({
                    Utils.dialogMessage(
                        requireContext(),
                        "แก้ไขข้อมูลสำเร็จ"
                    )
                    CustomDialogLoading.dissProgressDialog()
                },1000)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                CustomDialogLoading.dissProgressDialog()
                Utils.dialogMessage(
                    requireContext(),
                    "error : ${databaseError.message}"
                )
            }

        })
    }

    private fun validatePermissions() {
        Dexter.withActivity(requireActivity())
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    launchCamera(actionPhotos)
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    AlertDialog.Builder(requireContext())
                        .setTitle(R.string.storage_permission_rationale_title)
                        .setMessage(R.string.storage_permition_rationale_message)
                        .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                            token!!.cancelPermissionRequest()
                        }
                        .setPositiveButton(android.R.string.ok) { dialog, _ ->
                            dialog.dismiss()
                            token!!.continuePermissionRequest()
                        }
                        .setOnDismissListener { token?.cancelPermissionRequest() }
                        .show()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Snackbar.make(
                        viewDriver,
                        R.string.storage_permission_denied_message,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }).check()
    }

    private fun launchCamera(actionType:String) {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        val fileUri = requireActivity().contentResolver
            .insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            if (actionType == actionPhotos)
                mCurrentPhotoPath = fileUri.toString()
            else
                mCurrentPhotoPathCard = fileUri.toString()

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            startActivityForResult(
                intent,
                if (actionType == actionPhotos) TAKE_PHOTO_REQUEST else TAKE_PHOTO_CARD_REQUEST
            )
        }
    }


    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST) {
            processCapturedPhoto(actionPhotos)
        }else if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_CARD_REQUEST) {
            processCapturedPhoto(actionPhotosCard)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun processCapturedPhoto(actionPhotos: String) {
        val cursor = requireActivity().contentResolver.query(
            Uri.parse(if (actionPhotos == this.actionPhotos) mCurrentPhotoPath else mCurrentPhotoPathCard),
            Array(1) { android.provider.MediaStore.Images.ImageColumns.DATA },
            null, null, null
        )
        cursor?.moveToFirst()
        val photoPath = cursor?.getString(0)
        cursor?.close()
        val file = File(photoPath)
        if (actionPhotos == this.actionPhotos) {
            uriImageView = Uri.fromFile(file)
            ivProfile.setImageURI(uriImageView)
        }else {
            uriImageViewCard = Uri.fromFile(file)
            ivCard.setImageURI(uriImageViewCard)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileUserFragment()
    }
}