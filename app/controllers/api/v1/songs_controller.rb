module Api::V1
  class SongsController < BaseController
    def index
      @songs = Song.all
      render json: @songs
    end

    def create
      @song = Song.new(song_params)

      if @song.save
        render json: {status: 'ok'}
      else
        render json: {errors: @song.errors.full_messages}, status: :bad_request
      end
    end

    def remove
      Song.where(device_id: params[:device_id]).destroy_all
      render json: {status: 'ok'}
    end

    private

    def song_params
      params.require(:song).permit(:device_id, :name, :artist)
    end
  end
end
