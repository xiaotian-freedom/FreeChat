package com.common.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.common.R;

public class MapDialog extends Dialog {

    public static final int BUTTON_GO = -1;

    public static final int BUTTON_BUSINESS = -3;

    public MapDialog(Context context) {
        super(context);
    }

    public MapDialog(Context context, int theme) {
        super(context, theme);
    }

    public MapDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    /**
     * Helper class for creating a custom dialog
     **/
    public static class Builder {

        private Context mContext;
        private float mWidthRate = 0.87F;
        private float dimAmount = 0.6f;
        private String comName;
        private String address;
        private String[] tels;
        private boolean isBusiness;

        private OnClickListener goClickListener;
        private OnClickListener dialClickListener;
        private OnClickListener businessClickListener;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setWidthRate(float rate) {
            mWidthRate = rate;
            return this;
        }

        public Builder setDimAmount(float dimAmount) {
            this.dimAmount = dimAmount;
            return this;
        }

        public Builder setComName(String comName) {
            this.comName = comName;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setTelList(String[] tels) {
            this.tels = tels;
            return this;
        }

        public Builder showBusiness(boolean isBusiness) {
            this.isBusiness = isBusiness;
            return this;
        }

        public Builder setGoClickListener(OnClickListener goClickListener) {
            this.goClickListener = goClickListener;
            return this;
        }

        public Builder setDialClickListener(OnClickListener dialOneClickListener) {
            this.dialClickListener = dialOneClickListener;
            return this;
        }

        public Builder setBusinessClickListener(OnClickListener businessClickListener) {
            this.businessClickListener = businessClickListener;
            return this;
        }

        /**
         * Create the custom dialog
         */
        @SuppressLint("InflateParams")
        public MapDialog create() {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final MapDialog dialog = new MapDialog(mContext,
                    R.style.BaseDialog);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnim);

            LinearLayout layout = (LinearLayout) inflater
                    .inflate(R.layout.map_detail_layout, null);

            ImageButton ibClose = (ImageButton) layout.findViewById(R.id.map_detail_ib_close);
            ibClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            TextView tvComName = (TextView) layout.findViewById(R.id.map_detail_com_name);
            if (!TextUtils.isEmpty(comName)) {
                tvComName.setText(comName);
            }

            TextView tvAddress = (TextView) layout.findViewById(R.id.map_detail_address);
            if (!TextUtils.isEmpty(address)) {
                String text = mContext.getString(R.string.map_address_format, address);
                tvAddress.setText(text);
            }
            TextView tvGo = (TextView) layout.findViewById(R.id.map_detail_go);
            tvGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (goClickListener != null) {
                        goClickListener.onClick(dialog, BUTTON_GO);
                    }
                    dialog.dismiss();
                }
            });

            ListView telListView = (ListView) layout.findViewById(R.id.map_detail_tel);
            if (null != tels && tels.length > 0) {
                telListView.setVisibility(View.VISIBLE);
                telListView.setAdapter(new TelAdapter(dialog));
            }

            TextView tvBusiness = (TextView) layout.findViewById(R.id.map_detail_tv_business);
            if (isBusiness) {
                tvBusiness.setVisibility(View.VISIBLE);
            }
            tvBusiness.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (businessClickListener != null) {
                        businessClickListener.onClick(dialog, BUTTON_BUSINESS);
                    }
                    dialog.dismiss();
                }
            });

            dialog.addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));

            Window dialogWindow = dialog.getWindow();
            DisplayMetrics dm = new DisplayMetrics();
            dialogWindow.getWindowManager().getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (dm.widthPixels * mWidthRate);
            lp.height = LayoutParams.WRAP_CONTENT;
            lp.dimAmount = dimAmount;
            dialogWindow.setAttributes(lp);
            dialogWindow.setGravity(Gravity.CENTER);

            return dialog;
        }

        private class TelAdapter extends BaseAdapter {

            MapDialog dialog;

            public TelAdapter(MapDialog dialog) {
                this.dialog = dialog;
            }

            @Override
            public int getCount() {
                return tels.length;
            }

            @Override
            public Object getItem(int position) {
                return tels[position];
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                TelHolder telHolder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.map_detail_tel_item, parent, false);
                    telHolder = new TelHolder(convertView);
                    convertView.setTag(telHolder);
                } else {
                    telHolder = (TelHolder) convertView.getTag();
                }
                telHolder.tvTel.setText(tels[position]);
                telHolder.tvDial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialClickListener != null) {
                            dialClickListener.onClick(dialog, position);
                        }
                        dialog.dismiss();
                    }
                });
                return convertView;
            }
        }

        private class TelHolder {

            TextView tvTel;
            TextView tvDial;

            public TelHolder(View itemView) {
                tvTel = (TextView) itemView.findViewById(R.id.map_detail_tv_tel);
                tvDial = (TextView) itemView.findViewById(R.id.map_detail_tv_tel_dial);
            }
        }
    }
}
