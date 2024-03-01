package com.easyrent.abccarapp.abccar.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.easyrent.abccarapp.abccar.R
import com.easyrent.abccarapp.abccar.databinding.FragmentAboutBinding
import com.easyrent.abccarapp.abccar.manager.AccountInfoManager
import com.easyrent.abccarapp.abccar.ui.adapter.AboutItem
import com.easyrent.abccarapp.abccar.ui.adapter.AboutLinkListAdapter

class AboutFragment : BaseFragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    private val socialItemAdapter = AboutLinkListAdapter(getSocialItemList())
    private val contactUsItemAdapter = AboutLinkListAdapter(getContactUsItemList())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)

        initMenu()
        initView()

        return binding.root
    }

    private fun initView() {

        binding.tvEditpassword.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_about_to_changepassword)
        }

        binding.logout.setOnClickListener {
            logout()
        }

        binding.tvClearOriginalToken.setOnClickListener {
            AccountInfoManager.setOriginalToken("")
        }
        binding.tvClearAuthToken.setOnClickListener {
            AccountInfoManager.setAuthToken("")
        }

        binding.rvSocialItemList.apply {
            adapter = socialItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    RecyclerView.VERTICAL
                )
            )
        }

        binding.rvContactUsItemList.apply {
            adapter = contactUsItemAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    RecyclerView.VERTICAL
                )
            )
        }

        binding.tvFeedback.setOnClickListener {
            openWebSite("https://www.abccar.com.tw/mobile/contactus")
        }

        binding.tvTerms.setOnClickListener {
            openWebSite("https://www.abccar.com.tw/member_terms")
        }

        binding.tvPrivacy.setOnClickListener {
            openWebSite("https://www.abccar.com.tw/privacy")
        }


    }

    private fun initMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getSocialItemList(): List<AboutItem> {
        return mutableListOf<AboutItem>().apply {
            // 官方網站
            add(AboutItem(R.drawable.ic_website_24px, "官方網站") {
                openWebSite("https://www.abccar.com.tw")
            })

            add(AboutItem(R.drawable.ic_about_youtube, "官方Youtube") {
                openWebSite("https://www.youtube.com/channel/UCDRjDDEGPjWtyHCGdG-Q5Uw/featured")
            })

            add(AboutItem(R.drawable.ic_about_line, "官方Line@") {
                openWebSite("https://line.me/R/ti/p/@791tjewa")
            })

            add(AboutItem(R.drawable.ic_about_facebook, "abc好車網粉絲團") {
                openWebSite("https://facebook.com/abccar.tw")
            })

            add(AboutItem(R.drawable.ic_about_facebook, "日系優質中古車交流社團") {
                openWebSite("https://www.youtube.com/channel/UCDRjDDEGPjWtyHCGdG-Q5Uw/featured")
            })

            add(AboutItem(R.drawable.ic_about_facebook, "平行輸入進口車交流社團") {
                openWebSite("https://www.facebook.com/groups/471462316689695")
            })
        }
    }

    private fun getContactUsItemList(): List<AboutItem> {
        return mutableListOf<AboutItem>().apply {
            add(AboutItem(R.drawable.ic_bug_report_24px, "App問題回報") {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("abccar.easyrent@gmail.com")) // 要用array包起來
                    putExtra(Intent.EXTRA_SUBJECT, "問題回報")
                }
                startActivity(intent)
            })

            add(AboutItem(R.drawable.ic_about_line, "Line即時回覆") {
                openWebSite("https://line.me/R/ti/p/%40mzw0418p")
            })

            add(AboutItem(R.drawable.ic_support_agent_24px, "客服聯絡電話") {
//                Toast.makeText(requireActivity(), "尚未開放", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:0222928060")
                }
                startActivity(intent)
            })
        }
    }

    private fun openWebSite(url: String) {
        val webSite: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webSite)
        startActivity(intent)
    }
}