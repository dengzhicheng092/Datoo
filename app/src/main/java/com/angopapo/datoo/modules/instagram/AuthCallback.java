package com.angopapo.datoo.modules.instagram;

import com.angopapo.datoo.models.others.InstagramUser;

public interface AuthCallback {
  void onSuccess(InstagramUser socialUser);

  void onError(Throwable error);

  void onCancel();
}
