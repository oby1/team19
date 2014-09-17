Rails.application.routes.draw do
  root to: 'songs#index'

  namespace :api, defaults: {format: :json} do
    namespace :v1 do
      resources :songs_controller do
        collection do
          delete :remove
        end
      end
    end
  end
end
